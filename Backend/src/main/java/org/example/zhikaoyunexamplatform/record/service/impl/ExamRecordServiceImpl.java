package org.example.zhikaoyunexamplatform.record.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.common.constant.Constant;
import org.example.zhikaoyunexamplatform.common.enums.RecordStatusEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.exam.entity.Exam;
import org.example.zhikaoyunexamplatform.exam.entity.ExamQuestion;
import org.example.zhikaoyunexamplatform.exam.mapper.ExamClassMapper;
import org.example.zhikaoyunexamplatform.exam.mapper.ExamMapper;
import org.example.zhikaoyunexamplatform.question.entity.Question;
import org.example.zhikaoyunexamplatform.question.mapper.ExamQuestionMapper;
import org.example.zhikaoyunexamplatform.question.mapper.QuestionMapper;
import org.example.zhikaoyunexamplatform.record.dto.SaveAnswerDTO;
import org.example.zhikaoyunexamplatform.record.entity.AnswerDetail;
import org.example.zhikaoyunexamplatform.record.entity.ExamRecord;
import org.example.zhikaoyunexamplatform.record.mapper.AnswerDetailMapper;
import org.example.zhikaoyunexamplatform.record.mapper.ExamRecordMapper;
import org.example.zhikaoyunexamplatform.record.service.ExamRecordService;
import org.example.zhikaoyunexamplatform.record.vo.*;
import org.example.zhikaoyunexamplatform.user.entity.User;
import org.example.zhikaoyunexamplatform.user.mapper.UserMapper;
import org.example.zhikaoyunexamplatform.classroom.entity.ClassRoom;
import org.example.zhikaoyunexamplatform.classroom.mapper.ClassRoomMapper;
import org.example.zhikaoyunexamplatform.wrongbook.entity.WrongBook;
import org.example.zhikaoyunexamplatform.wrongbook.mapper.WrongBookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ExamRecordServiceImpl implements ExamRecordService {

    private final ExamRecordMapper examRecordMapper;
    private final AnswerDetailMapper answerDetailMapper;
    private final ExamMapper examMapper;
    private final ExamClassMapper examClassMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final WrongBookMapper wrongBookMapper;
    private final ClassRoomMapper classRoomMapper;
    private final TransactionTemplate transactionTemplate;
    private final PlatformTransactionManager transactionManager;

    @Override
    public List<AvailableExamVO> getAvailableExams(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<Exam> exams = examMapper.selectAllExams();
        if (exams.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量预加载：所有考试的班级关联、用户记录、题目数量
        List<Long> allExamIds = exams.stream().map(Exam::getId).collect(Collectors.toList());
        Map<Long, List<Long>> examClassIdsMap = new HashMap<>();
        List<Map<String, Long>> ecPairs = examClassMapper.selectByExamIds(allExamIds);
        for (Map<String, Long> pair : ecPairs) {
            Long eid = pair.get("exam_id");
            Long cid = pair.get("class_id");
            examClassIdsMap.computeIfAbsent(eid, k -> new ArrayList<>()).add(cid);
        }
        List<ExamRecord> allUserRecords = examRecordMapper.selectByUserIdAndExamIds(userId, allExamIds);
        Map<Long, List<ExamRecord>> userRecordsMap = allUserRecords.stream()
                .collect(Collectors.groupingBy(ExamRecord::getExamId));
        List<ExamQuestion> allExamQuestions = examQuestionMapper.selectByExamIds(allExamIds);
        Map<Long, Long> questionCountMap = allExamQuestions.stream()
                .collect(Collectors.groupingBy(ExamQuestion::getExamId, Collectors.counting()));

        List<AvailableExamVO> result = new ArrayList<>();
        for (Exam exam : exams) {
            // 未发布的考试不显示给学生
            if (exam.getStatus().equals(Constant.EXAM_DRAFT)) {
                continue;
            }

            // 校验考试是否限制班级。当 t_exam_class 中有记录时，才限制班级
            List<Long> classIds = examClassIdsMap.getOrDefault(exam.getId(), Collections.emptyList());
            if (!classIds.isEmpty()) {
                if (user.getClassId() == null || !classIds.contains(user.getClassId())) {
                    continue;
                }
            }

            List<ExamRecord> existing = userRecordsMap.getOrDefault(exam.getId(), Collections.emptyList());

            AvailableExamVO vo = new AvailableExamVO();
            vo.setExamId(exam.getId());
            vo.setTitle(exam.getTitle());
            vo.setDescription(exam.getDescription());
            vo.setDuration(exam.getDuration());
            vo.setTotalScore(exam.getTotalScore());
            vo.setPassScore(exam.getPassScore());
            vo.setStartTime(exam.getStartTime());
            vo.setEndTime(exam.getEndTime());
            vo.setAllowRetry(exam.getAllowRetry());
            vo.setQuestionCount(questionCountMap.getOrDefault(exam.getId(), 0L).intValue());
            vo.setStatus(exam.getStatus());

            // 1. 计算时间状态 (examState): 0未发布, 1未开始, 2进行中, 3已结束
            LocalDateTime now = LocalDateTime.now();
            Integer examState;
            if (exam.getStatus().equals(Constant.EXAM_DRAFT)) {
                examState = 0;
            } else if (exam.getStatus().equals(Constant.EXAM_FINISHED)) {
                examState = 3;
            } else if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
                examState = 1;
            } else if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
                examState = 3;
            } else {
                examState = 2;
            }
            vo.setExamState(examState);

            // 2. 获取用户参与状态 (userStatus): -1未参加, 0答题中, 1已提交
            Integer userStatus = -1;
            if (!existing.isEmpty()) {
                boolean hasProcessing = existing.stream().anyMatch(r -> r.getStatus().equals(Constant.RECORD_PROCESSING));
                boolean hasSubmitted = existing.stream().anyMatch(r ->
                        r.getStatus().equals(Constant.RECORD_SUBMITTED) || r.getStatus().equals(Constant.RECORD_TIMEOUT));
                if (hasProcessing) {
                    userStatus = 0;
                } else if (hasSubmitted) {
                    userStatus = 1;
                }
            }
            vo.setUserStatus(userStatus);

            result.add(vo);
        }
        return result;
    }

    @Override
    public ExamPaperVO enterExam(Long userId, Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        if (!exam.getStatus().equals(Constant.EXAM_ONGOING)) {
            throw new BusinessException("考试未开始或已结束");
        }
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new BusinessException("考试尚未开始");
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new BusinessException("考试已结束");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户信息异常");
        }

        // 校验考试班级范围限制
        int totalBind = examClassMapper.countByExamId(examId);
        if (totalBind > 0) {
            if (user.getClassId() == null) {
                throw new BusinessException("您不在该考试的班级范围内");
            }
            int classMatch = examClassMapper.countByExamIdAndClassId(examId, user.getClassId());
            if (classMatch == 0) {
                throw new BusinessException("您不在该考试的班级范围内");
            }
        }

        List<ExamRecord> existing = examRecordMapper.selectByUserIdAndExamId(userId, examId);
        boolean hasProcessing = existing.stream().anyMatch(r -> r.getStatus().equals(Constant.RECORD_PROCESSING));
        if (hasProcessing) {
            ExamRecord processingRecord = existing.stream()
                    .filter(r -> r.getStatus().equals(Constant.RECORD_PROCESSING))
                    .findFirst().orElseThrow();
            LocalDateTime deadline = processingRecord.getStartTime().plusMinutes(exam.getDuration());
            if (LocalDateTime.now().isAfter(deadline)) {
                doGradeAndSubmit(processingRecord, exam, Constant.RECORD_TIMEOUT);
                throw new BusinessException("考试已超时，无法继续答题");
            }
            return buildExamPaper(processingRecord, exam);
        }

        boolean hasSubmitted = existing.stream().anyMatch(r ->
                r.getStatus().equals(Constant.RECORD_SUBMITTED) || r.getStatus().equals(Constant.RECORD_TIMEOUT));
        if (hasSubmitted) {
            if (exam.getAllowRetry().equals(Constant.NO)) {
                throw new BusinessException("您已参加过该考试，且不允许重考");
            }
            long submittedCount = existing.stream().filter(r ->
                    r.getStatus().equals(Constant.RECORD_SUBMITTED) || r.getStatus().equals(Constant.RECORD_TIMEOUT)).count();
            if (exam.getMaxCount() != null && exam.getMaxCount() > 0 && submittedCount >= exam.getMaxCount()) {
                throw new BusinessException("已达到最大重试次数（" + exam.getMaxCount() + "次），无法再次参加考试");
            }
        }

        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setExamId(examId);
        record.setStatus(Constant.RECORD_PROCESSING);
        record.setScore(0);
        record.setStartTime(LocalDateTime.now());
        record.setVersion(0);
        examRecordMapper.insert(record);

        log.info("学生[userId={}]进入考试[examId={}]，recordId={}", userId, examId, record.getId());
        return buildExamPaper(record, exam);
    }

    @Override
    public void saveAnswer(Long userId, Long recordId, SaveAnswerDTO dto) {
        ExamRecord record = getAndValidateRecord(userId, recordId);
        Exam exam = examMapper.selectById(record.getExamId());

        AnswerDetail existing = answerDetailMapper.selectByRecordIdAndQuestionId(recordId, dto.getQuestionId());
        if (existing != null) {
            existing.setUserAnswer(dto.getAnswer().toUpperCase());
            answerDetailMapper.updateAnswer(existing);
        } else {
            AnswerDetail detail = new AnswerDetail();
            detail.setRecordId(recordId);
            detail.setQuestionId(dto.getQuestionId());
            detail.setUserAnswer(dto.getAnswer().toUpperCase());
            detail.setIsCorrect(Constant.NO);
            detail.setScore(0);
            answerDetailMapper.insert(detail);
        }

        if (checkTimeoutIndependent(record, exam)) {
            throw new BusinessException(400, "考试已超时，系统已自动交卷");
        }
    }

    @Override
    public Map<String, Object> tabSwitch(Long userId, Long recordId) {
        ExamRecord record = getAndValidateRecord(userId, recordId);
        Exam exam = examMapper.selectById(record.getExamId());
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        // 使用乐观锁防止并发切屏请求导致计数不准
        int rows = examRecordMapper.incrementTabSwitchWithVersion(recordId, record.getVersion());
        if (rows == 0) {
            throw new BusinessException("操作冲突，请重试");
        }

        if (checkTimeoutIndependent(record, exam)) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("submitted", true);
            result.put("currentCount", examRecordMapper.selectTabSwitchCount(recordId));
            result.put("maxCount", exam.getMaxTabSwitches() != null ? exam.getMaxTabSwitches() : 0);
            return result;
        }

        int maxSwitches = exam.getMaxTabSwitches() != null ? exam.getMaxTabSwitches() : 0;
        int currentCount = examRecordMapper.selectTabSwitchCount(recordId);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("currentCount", currentCount);
        result.put("maxCount", maxSwitches);

        if (maxSwitches > 0 && currentCount >= maxSwitches) {
            doGradeAndSubmit(record, exam, Constant.RECORD_SUBMITTED);
            result.put("submitted", true);
        } else {
            result.put("submitted", false);
        }

        return result;
    }

    @Override
    public Integer submitExam(Long userId, Long recordId) {
        ExamRecord record = getAndValidateRecord(userId, recordId);
        Exam exam = examMapper.selectById(record.getExamId());
        return doGradeAndSubmit(record, exam, Constant.RECORD_SUBMITTED);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ExamRecordVO> getMyRecords(Long userId, Integer page, Integer size) {
        int total = examRecordMapper.countByUserId(userId);
        int offset = (page - 1) * size;
        List<ExamRecord> records = examRecordMapper.selectPageByUserId(userId, offset, size);

        // 批量预加载考试信息，消除 N+1
        List<Long> examIds = records.stream().map(ExamRecord::getExamId).distinct().collect(Collectors.toList());
        Map<Long, Exam> examMap = examIds.isEmpty() ? Collections.emptyMap() :
                examMapper.selectByIds(examIds).stream().collect(Collectors.toMap(Exam::getId, e -> e));

        List<ExamRecordVO> voList = records.stream()
                .map(r -> toExamRecordVO(r, examMap.get(r.getExamId())))
                .collect(Collectors.toList());
        return PageResult.of((long) total, page, size, voList);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamRecordVO getRecordDetail(Long userId, Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("考试记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            User user = userMapper.selectById(userId);
            if (user == null || user.getRole().equals(Constant.ROLE_STUDENT)) {
                throw new BusinessException("无权查看该记录");
            }
        }

        if (record.getStatus().equals(Constant.RECORD_PROCESSING)) {
            ExamRecordVO processingVO = new ExamRecordVO();
            processingVO.setId(record.getId());
            processingVO.setExamId(record.getExamId());
            processingVO.setStatus(record.getStatus());
            processingVO.setStartTime(record.getStartTime());
            return processingVO;
        }

        Exam exam = examMapper.selectById(record.getExamId());
        ExamRecordVO vo = toExamRecordVO(record, exam);

        List<AnswerDetail> details = answerDetailMapper.selectByRecordId(recordId);
        // 批量预加载题目信息，消除 N+1
        List<Long> questionIds = details.stream().map(AnswerDetail::getQuestionId).distinct().collect(Collectors.toList());
        Map<Long, Question> questionMap = questionIds.isEmpty() ? Collections.emptyMap() :
                questionMapper.selectByIds(questionIds).stream().collect(Collectors.toMap(Question::getId, q -> q));

        List<AnswerDetailVO> answerVOs = details.stream()
                .map(d -> toAnswerDetailVO(d, questionMap.get(d.getQuestionId())))
                .collect(Collectors.toList());
        vo.setAnswerDetails(answerVOs);
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ExamRecordVO> getExamRecords(Long examId, Integer page, Integer size) {
        int total = examRecordMapper.countByExamId(examId);
        int offset = (page - 1) * size;
        List<ExamRecord> records = examRecordMapper.selectPageByExamId(examId, offset, size);

        // 批量预加载考试和用户信息，消除 N+1
        Exam exam = examMapper.selectById(examId);
        List<Long> userIds = records.stream().map(ExamRecord::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap() :
                userMapper.selectByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));

        List<ExamRecordVO> voList = records.stream().map(r -> {
            ExamRecordVO vo = toExamRecordVO(r, exam);
            User user = userMap.get(r.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName());
                vo.setStudentNo(user.getStudentNo());
            }
            return vo;
        }).collect(Collectors.toList());
        return PageResult.of((long) total, page, size, voList);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamScoreStatVO getExamScoreStat(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        List<ExamRecord> records = examRecordMapper.selectSubmittedByExamId(examId);
        ExamScoreStatVO vo = new ExamScoreStatVO();
        vo.setExamId(examId);
        vo.setExamTitle(exam.getTitle());
        vo.setTotalStudents(examRecordMapper.countByExamId(examId));
        vo.setSubmittedCount(records.size());
        if (records.isEmpty()) {
            vo.setAvgScore(0.0);
            vo.setMaxScore(0);
            vo.setMinScore(0);
            vo.setPassRate(0.0);
        } else {
            vo.setAvgScore(records.stream().mapToInt(ExamRecord::getScore).average().orElse(0.0));
            vo.setMaxScore(records.stream().mapToInt(ExamRecord::getScore).max().orElse(0));
            vo.setMinScore(records.stream().mapToInt(ExamRecord::getScore).min().orElse(0));
            long passCount = records.stream().filter(r -> r.getScore() >= exam.getPassScore()).count();
            vo.setPassRate((double) passCount / records.size() * 100);
        }
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<StudentScoreVO> getExamScoreRank(Long examId, Integer page, Integer size) {
        int total = examRecordMapper.countSubmittedByExamId(examId);
        int offset = (page - 1) * size;
        List<ExamRecord> records = examRecordMapper.selectSubmittedPageByExamId(examId, offset, size);

        // 批量预加载用户和班级信息，消除 N+1
        List<Long> userIds = records.stream().map(ExamRecord::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap() :
                userMapper.selectByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        Set<Long> classIds = userMap.values().stream()
                .map(User::getClassId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, ClassRoom> classMap = classIds.isEmpty() ? Collections.emptyMap() :
                classRoomMapper.selectByIds(new ArrayList<>(classIds)).stream()
                        .collect(Collectors.toMap(ClassRoom::getId, c -> c));

        List<StudentScoreVO> voList = records.stream().map(r -> {
            StudentScoreVO vo = new StudentScoreVO();
            vo.setRecordId(r.getId());
            vo.setScore(r.getScore());
            vo.setStatus(r.getStatus());
            vo.setStatusName(RecordStatusEnum.fromValue(r.getStatus()).getName());
            vo.setSubmitTime(r.getSubmitTime());
            User user = userMap.get(r.getUserId());
            if (user != null) {
                vo.setUserId(user.getId());
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName());
                vo.setStudentNo(user.getStudentNo());
                if (user.getClassId() != null) {
                    ClassRoom cr = classMap.get(user.getClassId());
                    if (cr != null) {
                        vo.setClassName(cr.getClassName());
                    }
                }
            }
            return vo;
        }).collect(Collectors.toList());
        return PageResult.of((long) total, page, size, voList);
    }

    @Override
    public void autoSubmitTimeoutRecords() {
        List<ExamRecord> processingRecords = examRecordMapper.selectProcessingRecords();
        if (processingRecords.isEmpty()) return;

        // 批量预加载考试信息，消除 N+1
        List<Long> examIds = processingRecords.stream()
                .map(ExamRecord::getExamId).distinct().collect(Collectors.toList());
        Map<Long, Exam> examMap = examMapper.selectByIds(examIds).stream()
                .collect(Collectors.toMap(Exam::getId, e -> e));

        for (ExamRecord record : processingRecords) {
            try {
                Exam exam = examMap.get(record.getExamId());
                if (exam == null) continue;

                LocalDateTime deadline = record.getStartTime().plusMinutes(exam.getDuration());
                if (LocalDateTime.now().isAfter(deadline)) {
                    transactionTemplate.executeWithoutResult(status ->
                            doGradeAndSubmit(record, exam, Constant.RECORD_TIMEOUT));
                    log.info("超时自动交卷：recordId={}, userId={}, examId={}", record.getId(), record.getUserId(), record.getExamId());
                }
            } catch (Exception e) {
                log.error("自动交卷失败：recordId={}, error={}", record.getId(), e.getMessage());
            }
        }
    }

    // ========== 私有方法 ==========

    private Integer doGradeAndSubmit(ExamRecord record, Exam exam, Integer submitStatus) {
        List<ExamQuestion> examQuestions = examQuestionMapper.selectByExamId(exam.getId());
        List<AnswerDetail> answerDetails = answerDetailMapper.selectByRecordId(record.getId());

        // 批量加载所有题目，消除循环内 N+1 查询
        List<Long> questionIds = examQuestions.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
        Map<Long, Question> questionMap = questionIds.isEmpty() ? Collections.emptyMap() :
                questionMapper.selectByIds(questionIds).stream()
                        .collect(Collectors.toMap(Question::getId, q -> q));

        // 批量加载该用户的错题本记录，消除循环内 N+1 查询
        Map<Long, WrongBook> wrongBookMap = questionIds.isEmpty() ? new HashMap<>() :
                wrongBookMapper.selectByUserIdAndQuestionIds(record.getUserId(), questionIds).stream()
                        .collect(Collectors.toMap(WrongBook::getQuestionId, wb -> wb));

        Map<Long, ExamQuestion> eqMap = examQuestions.stream()
                .collect(Collectors.toMap(ExamQuestion::getQuestionId, eq -> eq));
        Map<Long, AnswerDetail> adMap = answerDetails.stream()
                .collect(Collectors.toMap(AnswerDetail::getQuestionId, ad -> ad, (a, b) -> a));

        int totalScore = 0;

        for (ExamQuestion eq : examQuestions) {
            AnswerDetail ad = adMap.get(eq.getQuestionId());
            Question question = questionMap.get(eq.getQuestionId());
            if (question == null) continue;

            if (ad == null || ad.getUserAnswer() == null || ad.getUserAnswer().isBlank()) {
                if (ad != null) {
                    ad.setIsCorrect(Constant.NO);
                    ad.setScore(0);
                    answerDetailMapper.updateCorrect(ad);
                }
                writeWrongBook(record.getUserId(), record.getId(), question, ad != null ? ad.getUserAnswer() : null, wrongBookMap);
                continue;
            }

            if (question.getType().equals(Constant.QUESTION_MULTI)) {
                // 多选题：全对满分，漏选得一半，有错选0分
                char[] userChars = ad.getUserAnswer().toCharArray();
                Arrays.sort(userChars);
                String sortedUser = new String(userChars);
                String correctAnswer = question.getAnswer();

                char[] correctChars = correctAnswer.toCharArray();
                Arrays.sort(correctChars);
                String sortedCorrect = new String(correctChars);

                if (sortedUser.equals(sortedCorrect)) {
                    totalScore += eq.getScore();
                    ad.setIsCorrect(Constant.YES);
                    ad.setScore(eq.getScore());
                } else {
                    boolean isSubset = true;
                    for (char c : sortedUser.toCharArray()) {
                        if (sortedCorrect.indexOf(c) == -1) {
                            isSubset = false;
                            break;
                        }
                    }
                    if (isSubset && sortedUser.length() > 0 && sortedUser.length() < sortedCorrect.length()) {
                        ad.setIsCorrect(Constant.NO);
                        int halfScore = eq.getScore() % 2 == 0 ? eq.getScore() / 2 : (eq.getScore() + 1) / 2;
                        ad.setScore(halfScore);
                        totalScore += halfScore;
                    } else {
                        ad.setIsCorrect(Constant.NO);
                        ad.setScore(0);
                    }
                    writeWrongBook(record.getUserId(), record.getId(), question, ad.getUserAnswer(), wrongBookMap);
                }
            } else {
                // 单选/判断：直接比较
                boolean isCorrect = ad.getUserAnswer().equals(question.getAnswer());
                if (isCorrect) {
                    totalScore += eq.getScore();
                    ad.setIsCorrect(Constant.YES);
                    ad.setScore(eq.getScore());
                } else {
                    ad.setIsCorrect(Constant.NO);
                    ad.setScore(0);
                    writeWrongBook(record.getUserId(), record.getId(), question, ad.getUserAnswer(), wrongBookMap);
                }
            }
            answerDetailMapper.updateCorrect(ad);
        }

        record.setStatus(submitStatus);
        record.setScore(totalScore);
        record.setSubmitTime(LocalDateTime.now());
        int rows = examRecordMapper.updateStatusAndScore(record);
        if (rows == 0) {
            throw new BusinessException("交卷失败，请勿重复提交");
        }

        log.info("交卷成功：recordId={}, score={}, status={}", record.getId(), totalScore, submitStatus);
        return totalScore;
    }

    /**
     * 检查超时并使用独立事务自动交卷（避免被外层事务回滚）
     */
    private boolean checkTimeoutIndependent(ExamRecord record, Exam exam) {
        LocalDateTime deadline = record.getStartTime().plusMinutes(exam.getDuration());
        if (LocalDateTime.now().isAfter(deadline)) {
            TransactionTemplate newTx = new TransactionTemplate(transactionManager);
            newTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            newTx.executeWithoutResult(status -> doGradeAndSubmit(record, exam, Constant.RECORD_TIMEOUT));
            return true;
        }
        return false;
    }

    private ExamRecord getAndValidateRecord(Long userId, Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("考试记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该考试记录");
        }
        if (!record.getStatus().equals(Constant.RECORD_PROCESSING)) {
            throw new BusinessException("考试已结束，无法操作");
        }
        return record;
    }

    /**
     * 写入错题本（使用预加载的 wrongBookMap 避免 N+1 查询）
     */
    private void writeWrongBook(Long userId, Long recordId, Question question, String myAnswer, Map<Long, WrongBook> wrongBookMap) {
        try {
            WrongBook existing = wrongBookMap.get(question.getId());
            if (existing != null) {
                wrongBookMapper.incrementWrongCount(existing.getId());
            } else {
                WrongBook wb = new WrongBook();
                wb.setUserId(userId);
                wb.setQuestionId(question.getId());
                wb.setRecordId(recordId);
                wb.setMyAnswer(myAnswer);
                wb.setWrongCount(1);
                wb.setMastered(Constant.NO);
                wrongBookMapper.insert(wb);
                // 更新缓存，同一事务内后续引用可命中
                wrongBookMap.put(question.getId(), wb);
            }
        } catch (org.springframework.dao.DuplicateKeyException e) {
            WrongBook existing = wrongBookMapper.selectByUserIdAndQuestionId(userId, question.getId());
            if (existing != null) {
                wrongBookMapper.incrementWrongCount(existing.getId());
            }
        }
    }

    private ExamPaperVO buildExamPaper(ExamRecord record, Exam exam) {
        ExamPaperVO vo = new ExamPaperVO();
        vo.setRecordId(record.getId());
        vo.setExamTitle(exam.getTitle());
        vo.setDuration(exam.getDuration());
        vo.setMaxTabSwitches(exam.getMaxTabSwitches());

        LocalDateTime deadline = record.getStartTime().plusMinutes(exam.getDuration());
        long remainingSeconds = Duration.between(LocalDateTime.now(), deadline).getSeconds();
        vo.setRemainingSeconds(Math.max(0, remainingSeconds));

        List<ExamQuestion> examQuestions = examQuestionMapper.selectByExamId(exam.getId());
        List<AnswerDetail> existingAnswers = answerDetailMapper.selectByRecordId(record.getId());
        Map<Long, String> answerMap = existingAnswers.stream()
                .collect(Collectors.toMap(AnswerDetail::getQuestionId, AnswerDetail::getUserAnswer, (a, b) -> a));

        // 批量加载所有题目，消除循环内 N+1 查询
        List<Long> questionIds = examQuestions.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
        Map<Long, Question> questionMap = questionIds.isEmpty() ? Collections.emptyMap() :
                questionMapper.selectByIds(questionIds).stream()
                        .collect(Collectors.toMap(Question::getId, q -> q));

        List<QuestionItemVO> questionItems = new ArrayList<>();
        for (ExamQuestion eq : examQuestions) {
            Question q = questionMap.get(eq.getQuestionId());
            if (q == null) continue;

            QuestionItemVO item = new QuestionItemVO();
            item.setQuestionId(q.getId());
            item.setType(q.getType());
            item.setContent(q.getContent());
            item.setImage(q.getImage());
            item.setOptionA(q.getOptionA());
            item.setOptionB(q.getOptionB());
            item.setOptionC(q.getOptionC());
            item.setOptionD(q.getOptionD());
            item.setScore(eq.getScore());
            item.setSortOrder(eq.getSortOrder());
            item.setStudentAnswer(answerMap.get(q.getId()));
            questionItems.add(item);
        }
        vo.setQuestions(questionItems);
        return vo;
    }

    private ExamRecordVO toExamRecordVO(ExamRecord record, Exam exam) {
        ExamRecordVO vo = new ExamRecordVO();
        vo.setId(record.getId());
        vo.setExamId(record.getExamId());
        vo.setStatus(record.getStatus());
        vo.setStatusName(RecordStatusEnum.fromValue(record.getStatus()).getName());
        vo.setTotalScore(record.getScore());
        vo.setStartTime(record.getStartTime());
        vo.setSubmitTime(record.getSubmitTime());
        vo.setUserId(record.getUserId());

        if (exam != null) {
            vo.setExamTitle(exam.getTitle());
            vo.setDuration(exam.getDuration());
            vo.setPassScore(exam.getPassScore());
        }
        return vo;
    }

    private AnswerDetailVO toAnswerDetailVO(AnswerDetail detail, Question question) {
        AnswerDetailVO vo = new AnswerDetailVO();
        vo.setQuestionId(detail.getQuestionId());
        vo.setStudentAnswer(detail.getUserAnswer());
        vo.setIsCorrect(detail.getIsCorrect());
        vo.setScore(detail.getScore());

        if (question != null) {
            vo.setType(question.getType());
            vo.setContent(question.getContent());
            vo.setOptionA(question.getOptionA());
            vo.setOptionB(question.getOptionB());
            vo.setOptionC(question.getOptionC());
            vo.setOptionD(question.getOptionD());
            vo.setCorrectAnswer(question.getAnswer());
            vo.setExplanation(question.getExplanation());
        }
        return vo;
    }
}
