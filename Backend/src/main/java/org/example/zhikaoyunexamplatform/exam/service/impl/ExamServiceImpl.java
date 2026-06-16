package org.example.zhikaoyunexamplatform.exam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.common.constant.Constant;
import org.example.zhikaoyunexamplatform.common.enums.ExamStatusEnum;
import org.example.zhikaoyunexamplatform.common.enums.QuestionTypeEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.exam.dto.ExamCreateDTO;
import org.example.zhikaoyunexamplatform.exam.dto.ExamUpdateDTO;
import org.example.zhikaoyunexamplatform.exam.dto.RandomPaperDTO;
import org.example.zhikaoyunexamplatform.exam.entity.Exam;
import org.example.zhikaoyunexamplatform.exam.entity.ExamQuestion;
import org.example.zhikaoyunexamplatform.exam.mapper.ExamClassMapper;
import org.example.zhikaoyunexamplatform.exam.mapper.ExamMapper;
import org.example.zhikaoyunexamplatform.exam.service.ExamService;
import org.example.zhikaoyunexamplatform.exam.vo.ExamQuestionVO;
import org.example.zhikaoyunexamplatform.exam.vo.ExamVO;
import org.example.zhikaoyunexamplatform.question.mapper.ExamQuestionMapper;
import org.example.zhikaoyunexamplatform.question.entity.Question;
import org.example.zhikaoyunexamplatform.question.mapper.QuestionMapper;
import org.example.zhikaoyunexamplatform.classroom.entity.ClassRoom;
import org.example.zhikaoyunexamplatform.classroom.mapper.ClassRoomMapper;
import org.example.zhikaoyunexamplatform.record.mapper.ExamRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamClassMapper examClassMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;
    private final ClassRoomMapper classRoomMapper;
    private final ExamRecordMapper examRecordMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResult<ExamVO> listExams(Integer page, Integer size, Integer status, String keyword) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        Long creatorId = (currentRole != null && currentRole.equals(Constant.ROLE_ADMIN)) ? null : currentUserId;

        int total = examMapper.countByConditions(status, keyword, creatorId);
        int offset = (page - 1) * size;
        List<ExamVO> records = examMapper.selectPage(status, keyword, offset, size, creatorId);

        // 批量填充考试附加信息，消除 N+1（每个 exam 原本 4 次额外查询）
        batchFillExamVOs(records);

        return PageResult.of((long) total, page, size, records);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamVO getExamById(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        return fillExamVO(toExamVO(exam));
    }

    @Override
    public ExamVO createExam(ExamCreateDTO dto, Long creatorId) {
        Exam exam = new Exam();
        exam.setTitle(dto.getTitle());
        exam.setDescription(dto.getDescription());
        exam.setExamType(0);
        exam.setCreatorId(creatorId);
        exam.setDuration(dto.getDuration());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setStatus(Constant.EXAM_DRAFT);
        exam.setTotalScore(dto.getTotalScore());
        exam.setPassScore(dto.getPassScore());
        exam.setShowAnswer(dto.getShowAnswer());
        exam.setAllowRetry(dto.getAllowRetry());
        exam.setQuestionOrder(dto.getQuestionOrder());
        exam.setOptionOrder(dto.getOptionOrder());
        exam.setMaxCount(dto.getMaxCount());
        exam.setMaxTabSwitches(dto.getMaxTabSwitches());
        exam.setRemark(dto.getRemark());

        validateExamTime(dto.getStartTime(), dto.getEndTime(), dto.getDuration());

        examMapper.insert(exam);

        // 批量校验班级归属，消除 N+1
        if (dto.getClassIds() != null && !dto.getClassIds().isEmpty()) {
            Integer currentRole = SecurityUtils.getCurrentUserRole();
            List<ClassRoom> classes = classRoomMapper.selectByIds(dto.getClassIds());
            Map<Long, ClassRoom> classMap = classes.stream().collect(Collectors.toMap(ClassRoom::getId, c -> c));
            for (Long classId : dto.getClassIds()) {
                ClassRoom cr = classMap.get(classId);
                if (cr == null) {
                    throw new BusinessException("班级 ID=" + classId + " 不存在");
                }
                if (!Constant.ROLE_ADMIN.equals(currentRole) && !cr.getTeacherId().equals(creatorId)) {
                    throw new BusinessException("无权将考试关联到班级【" + cr.getClassName() + "】，您不是该班级的负责教师");
                }
                examClassMapper.insert(exam.getId(), classId);
            }
        }

        log.info("考试创建成功，id={}, title={}", exam.getId(), exam.getTitle());
        return fillExamVO(toExamVO(exam));
    }

    @Override
    public ExamVO updateExam(Long id, ExamUpdateDTO dto) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        if (!exam.getStatus().equals(Constant.EXAM_DRAFT)) {
            throw new BusinessException("只能修改未发布的考试");
        }

        exam.setTitle(dto.getTitle());
        exam.setDescription(dto.getDescription());
        exam.setDuration(dto.getDuration());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setTotalScore(dto.getTotalScore());
        exam.setPassScore(dto.getPassScore());
        exam.setShowAnswer(dto.getShowAnswer());
        exam.setAllowRetry(dto.getAllowRetry());
        exam.setQuestionOrder(dto.getQuestionOrder());
        exam.setOptionOrder(dto.getOptionOrder());
        exam.setMaxCount(dto.getMaxCount());
        exam.setMaxTabSwitches(dto.getMaxTabSwitches());
        exam.setRemark(dto.getRemark());

        validateExamTime(dto.getStartTime(), dto.getEndTime(), dto.getDuration());

        examMapper.updateById(exam);

        if (dto.getClassIds() != null) {
            Integer currentRole = SecurityUtils.getCurrentUserRole();
            // 批量校验班级，消除 N+1
            List<ClassRoom> classes = classRoomMapper.selectByIds(dto.getClassIds());
            Map<Long, ClassRoom> classMap = classes.stream().collect(Collectors.toMap(ClassRoom::getId, c -> c));
            for (Long classId : dto.getClassIds()) {
                ClassRoom cr = classMap.get(classId);
                if (cr == null) {
                    throw new BusinessException("班级 ID=" + classId + " 不存在");
                }
                if (!Constant.ROLE_ADMIN.equals(currentRole) && !cr.getTeacherId().equals(exam.getCreatorId())) {
                    throw new BusinessException("无权将考试关联到班级【" + cr.getClassName() + "】");
                }
            }
            examClassMapper.deleteByExamId(id);
            for (Long classId : dto.getClassIds()) {
                examClassMapper.insert(id, classId);
            }
        }

        log.info("考试修改成功，id={}", id);
        return fillExamVO(toExamVO(exam));
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        if (!exam.getStatus().equals(Constant.EXAM_DRAFT)) {
            throw new BusinessException("只能删除未发布的考试");
        }
        examMapper.deleteById(id);
        log.info("考试删除成功，id={}", id);
    }

    @Override
    public void publishExam(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        if (!exam.getStatus().equals(Constant.EXAM_DRAFT)) {
            throw new BusinessException("只能发布未发布的考试");
        }
        int questionCount = examQuestionMapper.countByExamId(id);
        if (questionCount == 0) {
            throw new BusinessException("考试没有题目，无法发布");
        }
        examMapper.updateStatus(id, Constant.EXAM_ONGOING);
        log.info("考试发布成功，id={}", id);
    }

    @Override
    public void endExam(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        if (!exam.getStatus().equals(Constant.EXAM_ONGOING)) {
            throw new BusinessException("只能结束进行中的考试");
        }
        examMapper.updateStatus(id, Constant.EXAM_FINISHED);
        log.info("考试结束成功，id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamQuestionVO> getExamQuestions(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        List<ExamQuestion> eqList = examQuestionMapper.selectByExamId(examId);

        // 批量加载所有题目，消除 N+1
        List<Long> questionIds = eqList.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
        Map<Long, Question> questionMap = questionIds.isEmpty() ? Collections.emptyMap() :
                questionMapper.selectByIds(questionIds).stream()
                        .collect(Collectors.toMap(Question::getId, q -> q));

        List<ExamQuestionVO> result = new ArrayList<>();
        for (ExamQuestion eq : eqList) {
            Question q = questionMap.get(eq.getQuestionId());
            if (q == null) continue;
            ExamQuestionVO vo = new ExamQuestionVO();
            vo.setId(eq.getId());
            vo.setQuestionId(q.getId());
            vo.setScore(eq.getScore());
            vo.setSortOrder(eq.getSortOrder());
            vo.setType(q.getType());
            vo.setContent(q.getContent());
            vo.setOptionA(q.getOptionA());
            vo.setOptionB(q.getOptionB());
            vo.setOptionC(q.getOptionC());
            vo.setOptionD(q.getOptionD());
            vo.setAnswer(q.getAnswer());
            vo.setExplanation(q.getExplanation());
            result.add(vo);
        }
        return result;
    }

    @Override
    public void addQuestionToExam(Long examId, Long questionId, Integer score) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        if (!exam.getStatus().equals(Constant.EXAM_DRAFT)) {
            throw new BusinessException("只能向未发布的考试添加题目");
        }
        Question q = questionMapper.selectById(questionId);
        if (q == null) {
            throw new BusinessException("题目不存在");
        }
        if (examQuestionMapper.countByExamIdAndQuestionId(examId, questionId) > 0) {
            throw new BusinessException("该题目已在考试中，请勿重复添加");
        }
        int maxSort = examQuestionMapper.selectByExamId(examId).stream()
                .mapToInt(ExamQuestion::getSortOrder).max().orElse(0);
        ExamQuestion eq = new ExamQuestion();
        eq.setExamId(examId);
        eq.setQuestionId(questionId);
        eq.setScore(score);
        eq.setSortOrder(maxSort + 1);
        examQuestionMapper.insert(eq);
        log.info("题目添加到考试，examId={}, questionId={}", examId, questionId);
    }

    @Override
    public void removeQuestionFromExam(Long examId, Long questionId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        if (!exam.getStatus().equals(Constant.EXAM_DRAFT)) {
            throw new BusinessException("只能从未发布的考试移除题目");
        }
        examQuestionMapper.deleteByExamIdAndQuestionId(examId, questionId);
        log.info("从考试移除题目，examId={}, questionId={}", examId, questionId);
    }

    @Override
    public List<ExamQuestionVO> randomPaper(Long examId, RandomPaperDTO dto) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        checkExamOwner(exam);
        if (!exam.getStatus().equals(Constant.EXAM_DRAFT)) {
            throw new BusinessException("只能对未发布的考试随机组卷");
        }

        examQuestionMapper.deleteByExamId(examId);

        List<ExamQuestionVO> result = new ArrayList<>();
        int sortOrder = 1;
        int totalScore = 0;

        // 收集所有待插入的 ExamQuestion，最后批量插入
        List<ExamQuestion> toInsert = new ArrayList<>();

        for (RandomPaperDTO.RandomRule rule : dto.getRules()) {
            List<Question> candidates = questionMapper.selectRandomByRule(
                    rule.getType(), rule.getDifficulty(), rule.getCategory(), rule.getCount());

            if (candidates.size() < rule.getCount()) {
                String typeName = QuestionTypeEnum.fromValue(rule.getType()).getName();
                String condition = typeName;
                if (rule.getDifficulty() != null) {
                    condition += switch (rule.getDifficulty()) {
                        case 1 -> "(简单)";
                        case 2 -> "(中等)";
                        case 3 -> "(困难)";
                        default -> "";
                    };
                }
                if (rule.getCategory() != null) {
                    condition += "[" + rule.getCategory() + "]";
                }
                throw new BusinessException("题库中 " + condition + " 不足 " + rule.getCount() + " 道，实际只有 " + candidates.size() + " 道");
            }

            for (Question q : candidates) {
                ExamQuestion eq = new ExamQuestion();
                eq.setExamId(examId);
                eq.setQuestionId(q.getId());
                eq.setScore(rule.getScore());
                eq.setSortOrder(sortOrder);
                toInsert.add(eq);

                ExamQuestionVO vo = new ExamQuestionVO();
                vo.setQuestionId(q.getId());
                vo.setScore(rule.getScore());
                vo.setSortOrder(sortOrder);
                vo.setType(q.getType());
                vo.setContent(q.getContent());
                vo.setOptionA(q.getOptionA());
                vo.setOptionB(q.getOptionB());
                vo.setOptionC(q.getOptionC());
                vo.setOptionD(q.getOptionD());
                vo.setAnswer(q.getAnswer());
                vo.setExplanation(q.getExplanation());
                result.add(vo);
                sortOrder++;
                totalScore += rule.getScore();
            }
        }

        // 批量插入替代逐条插入，消除 N+1
        if (!toInsert.isEmpty()) {
            examQuestionMapper.batchInsert(toInsert);
        }

        exam.setTotalScore(totalScore);
        examMapper.updateById(exam);

        log.info("随机组卷完成，examId={}, 题目总数={}, 总分={}", examId, result.size(), totalScore);
        return result;
    }

    // ========== 私有方法 ==========

    private void checkExamOwner(Exam exam) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        if (!Constant.ROLE_ADMIN.equals(currentRole) && !exam.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("只能操作自己创建的考试");
        }
    }

    private void validateExamTime(LocalDateTime startTime, LocalDateTime endTime, Integer duration) {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        if (duration == null || duration <= 0) {
            throw new BusinessException("考试时长必须大于0");
        }
    }

    private ExamVO toExamVO(Exam exam) {
        ExamVO vo = new ExamVO();
        vo.setId(exam.getId());
        vo.setTitle(exam.getTitle());
        vo.setDescription(exam.getDescription());
        vo.setExamType(exam.getExamType());
        vo.setCreatorId(exam.getCreatorId());
        vo.setDuration(exam.getDuration());
        vo.setStartTime(exam.getStartTime());
        vo.setEndTime(exam.getEndTime());
        vo.setStatus(exam.getStatus());
        vo.setStatusName(ExamStatusEnum.fromValue(exam.getStatus()).getName());
        vo.setTotalScore(exam.getTotalScore());
        vo.setPassScore(exam.getPassScore());
        vo.setShowAnswer(exam.getShowAnswer());
        vo.setAllowRetry(exam.getAllowRetry());
        vo.setQuestionOrder(exam.getQuestionOrder());
        vo.setOptionOrder(exam.getOptionOrder());
        vo.setMaxCount(exam.getMaxCount());
        vo.setMaxTabSwitches(exam.getMaxTabSwitches());
        vo.setRemark(exam.getRemark());
        vo.setCreateTime(exam.getCreateTime());
        return vo;
    }

    /** 填充单个考试的附加信息（用于单考试场景如 getExamById、createExam、updateExam） */
    private ExamVO fillExamVO(ExamVO vo) {
        List<Long> classIds = examClassMapper.selectClassIdsByExamId(vo.getId());
        vo.setClassIds(classIds);
        List<String> classNames = new ArrayList<>();
        if (!classIds.isEmpty()) {
            Map<Long, ClassRoom> classMap = classRoomMapper.selectByIds(classIds).stream()
                    .collect(Collectors.toMap(ClassRoom::getId, c -> c));
            for (Long classId : classIds) {
                ClassRoom cr = classMap.get(classId);
                if (cr != null) classNames.add(cr.getClassName());
            }
        }
        vo.setClassNames(classNames);
        vo.setQuestionCount(examQuestionMapper.countByExamId(vo.getId()));
        vo.setSubmittedCount(examRecordMapper.countSubmittedByExamId(vo.getId()));
        return vo;
    }

    /**
     * 批量填充多个考试的附加信息（用于列表查询，消除 4xN 的 N+1 查询）
     * 原本每个 exam 需要：1 次班级关联查询 + N 次班级名称查询 + 1 次题目数量 + 1 次提交数量
     * 现在改为：1 次批量班级关联 + 1 次批量班级名称 + 1 次批量题目数量 + 1 次批量提交数量
     */
    private void batchFillExamVOs(List<ExamVO> voList) {
        if (voList == null || voList.isEmpty()) return;

        List<Long> examIds = voList.stream().map(ExamVO::getId).collect(Collectors.toList());

        // 批量加载班级关联
        List<ExamQuestion> allEQ = examQuestionMapper.selectByExamIds(examIds);
        Map<Long, Long> questionCountMap = allEQ.stream()
                .collect(Collectors.groupingBy(ExamQuestion::getExamId, Collectors.counting()));

        // 批量加载班级关联（单次查询替代 N 次循环查询）
        Map<Long, List<Long>> examClassIdsMap = new HashMap<>();
        List<java.util.Map<String, Long>> ecPairs = examClassMapper.selectByExamIds(examIds);
        for (java.util.Map<String, Long> pair : ecPairs) {
            Long eid = pair.get("exam_id");
            Long cid = pair.get("class_id");
            examClassIdsMap.computeIfAbsent(eid, k -> new ArrayList<>()).add(cid);
        }

        // 批量加载所有涉及的班级名称
        Set<Long> allClassIds = new HashSet<>();
        examClassIdsMap.values().forEach(allClassIds::addAll);
        Map<Long, ClassRoom> classMap = allClassIds.isEmpty() ? Collections.emptyMap() :
                classRoomMapper.selectByIds(new ArrayList<>(allClassIds)).stream()
                        .collect(Collectors.toMap(ClassRoom::getId, c -> c));

        // 批量加载已提交数量（单次查询替代 N 次循环查询）
        Map<Long, Integer> submittedCountMap = new HashMap<>();
        List<java.util.Map<String, Object>> countRows = examRecordMapper.countSubmittedByExamIds(examIds);
        for (java.util.Map<String, Object> row : countRows) {
            Long eid = ((Number) row.get("exam_id")).longValue();
            Integer cnt = ((Number) row.get("cnt")).intValue();
            submittedCountMap.put(eid, cnt);
        }

        // 填充到每个 VO
        for (ExamVO vo : voList) {
            List<Long> classIds = examClassIdsMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setClassIds(classIds);
            List<String> classNames = new ArrayList<>();
            for (Long classId : classIds) {
                ClassRoom cr = classMap.get(classId);
                if (cr != null) classNames.add(cr.getClassName());
            }
            vo.setClassNames(classNames);
            vo.setQuestionCount(questionCountMap.getOrDefault(vo.getId(), 0L).intValue());
            vo.setSubmittedCount(submittedCountMap.getOrDefault(vo.getId(), 0));
        }
    }
}
