package org.example.zhikaoyunexamplatform.question.service.impl;

import com.alibaba.excel.EasyExcel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.common.constant.Constant;
import org.example.zhikaoyunexamplatform.common.enums.QuestionTypeEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.question.dto.QuestionDTO;
import org.example.zhikaoyunexamplatform.question.dto.QuestionExcelDTO;
import org.example.zhikaoyunexamplatform.question.entity.Question;
import org.example.zhikaoyunexamplatform.question.mapper.ExamQuestionMapper;
import org.example.zhikaoyunexamplatform.question.mapper.QuestionMapper;
import org.example.zhikaoyunexamplatform.question.service.QuestionService;
import org.example.zhikaoyunexamplatform.question.vo.ImportResultVO;
import org.example.zhikaoyunexamplatform.question.vo.QuestionVO;
import org.example.zhikaoyunexamplatform.question.vo.SelectionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final ExamQuestionMapper examQuestionMapper;

    @Override
    public PageResult<QuestionVO> listQuestions(Integer page, Integer size, Integer type,
                                                Integer difficulty, String category,
                                                String keyword, Integer auditStatus,
                                                Long currentUserId, Integer currentRole) {
        // 教师只看自己的题目，管理员看全部
        Long creatorId = currentRole.equals(Constant.ROLE_ADMIN) ? null : currentUserId;
        int total = questionMapper.countByConditions(type, difficulty, category, keyword, auditStatus, creatorId);
        int offset = (page - 1) * size;
        List<QuestionVO> records = questionMapper.selectPage(type, difficulty, category, keyword, auditStatus, offset, size, creatorId);
        for (QuestionVO vo : records) {
            vo.setTypeName(QuestionTypeEnum.fromValue(vo.getType()).getName());
            vo.setDifficultyName(difficultyName(vo.getDifficulty()));
            vo.setAuditStatusName(auditStatusName(vo.getAuditStatus()));
        }
        return PageResult.of((long) total, page, size, records);
    }

    @Override
    public QuestionVO getQuestionById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException(404, "题目不存在");
        }
        return toQuestionVO(question);
    }

    @Override
    public Question createQuestion(QuestionDTO dto, Long creatorId) {
        String answer = validateAndNormalizeAnswer(dto.getType(), dto.getAnswer());
        if (dto.getType() == 2) {
            dto.setOptionC(null);
            dto.setOptionD(null);
        }

        Question question = new Question();
        question.setType(dto.getType());
        question.setContent(dto.getContent());
        question.setImage(dto.getImage());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setAnswer(answer);
        question.setExplanation(dto.getExplanation());
        question.setDifficulty(dto.getDifficulty());
        question.setCategory(dto.getCategory());
        question.setCreatorId(creatorId);
        question.setAuditStatus(0);

        questionMapper.insert(question);
        log.info("题目创建成功，id={}", question.getId());
        return question;
    }

    @Override
    public void updateQuestion(Long id, QuestionDTO dto, Long currentUserId, Integer currentRole) {
        Question existing = questionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "题目不存在");
        }
        // 教师只能修改自己的题目
        if (!currentRole.equals(Constant.ROLE_ADMIN) && !existing.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("只能修改自己创建的题目");
        }

        String answer = validateAndNormalizeAnswer(dto.getType(), dto.getAnswer());

        Question update = new Question();
        update.setId(id);
        update.setType(dto.getType());
        update.setContent(dto.getContent());
        update.setImage(dto.getImage());
        update.setOptionA(dto.getOptionA());
        update.setOptionB(dto.getOptionB());
        update.setOptionC(dto.getType() == 2 ? null : dto.getOptionC());
        update.setOptionD(dto.getType() == 2 ? null : dto.getOptionD());
        update.setAnswer(answer);
        update.setExplanation(dto.getExplanation());
        update.setDifficulty(dto.getDifficulty());
        update.setCategory(dto.getCategory());

        questionMapper.updateById(update);
        log.info("题目修改成功，id={}", id);
    }

    @Override
    public void deleteQuestion(Long id, Long currentUserId, Integer currentRole) {
        Question existing = questionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "题目不存在");
        }
        // 教师只能删除自己的题目
        if (!currentRole.equals(Constant.ROLE_ADMIN) && !existing.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("只能删除自己创建的题目");
        }
        if (examQuestionMapper.countByQuestionId(id) > 0) {
            throw new BusinessException(400, "该题目已被考试引用，无法删除");
        }
        questionMapper.deleteById(id);
        log.info("题目删除成功，id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultVO importExcel(MultipartFile file, Long creatorId) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new BusinessException("请上传 .xlsx 格式的文件");
        }

        ImportResultVO result = new ImportResultVO();

        try {
            List<QuestionExcelDTO> list = EasyExcel.read(file.getInputStream())
                    .head(QuestionExcelDTO.class)
                    .sheet()
                    .doReadSync();

            for (int i = 0; i < list.size(); i++) {
                int rowNum = i + 1;
                QuestionExcelDTO row = list.get(i);
                try {
                    validateRow(row);
                    String answer = validateAndNormalizeAnswer(row.getType(), row.getAnswer());

                    Question question = new Question();
                    question.setType(row.getType());
                    question.setContent(row.getContent());
                    question.setImage(row.getImage());
                    question.setOptionA(row.getOptionA());
                    question.setOptionB(row.getOptionB());
                    question.setOptionC(row.getType() == 2 ? null : row.getOptionC());
                    question.setOptionD(row.getType() == 2 ? null : row.getOptionD());
                    question.setAnswer(answer);
                    question.setExplanation(row.getExplanation());
                    question.setDifficulty(row.getDifficulty());
                    question.setCategory(row.getCategory());
                    question.setCreatorId(creatorId);
                    question.setAuditStatus(0);

                    questionMapper.insert(question);
                    result.addSuccess();
                } catch (Exception e) {
                    result.addFail(rowNum, e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败");
        }

        log.info("Excel导入完成，成功={}，失败={}", result.getSuccessCount(), result.getFailCount());
        return result;
    }

    @Override
    public SelectionVO getSelections() {
        SelectionVO vo = new SelectionVO();

        List<SelectionVO.OptionItem> types = Arrays.asList(
                createOption(0, "单选题"),
                createOption(1, "多选题"),
                createOption(2, "判断题"));
        vo.setTypes(types);

        List<SelectionVO.OptionItem> difficulties = Arrays.asList(
                createOption(1, "简单"),
                createOption(2, "中等"),
                createOption(3, "困难"));
        vo.setDifficulties(difficulties);

        vo.setCategories(questionMapper.selectDistinctCategories());
        return vo;
    }

    private SelectionVO.OptionItem createOption(Integer value, String label) {
        SelectionVO.OptionItem item = new SelectionVO.OptionItem();
        item.setValue(value);
        item.setLabel(label);
        return item;
    }

    private String validateAndNormalizeAnswer(Integer type, String rawAnswer) {
        String answer = rawAnswer.toUpperCase();
        switch (type) {
            case 0 -> {
                if (!answer.matches("[A-D]")) {
                    throw new BusinessException("单选题答案必须是单个 A/B/C/D");
                }
            }
            case 1 -> {
                if (answer.length() < 2 || answer.length() > 4 || !answer.matches("[A-D]+")) {
                    throw new BusinessException("多选题答案格式错误");
                }
                char[] chars = answer.toCharArray();
                Arrays.sort(chars);
                answer = new String(chars);
            }
            case 2 -> {
                if (!answer.equals("A") && !answer.equals("B")) {
                    throw new BusinessException("判断题答案必须是 A 或 B");
                }
            }
            default -> throw new BusinessException("无效的题目类型");
        }
        return answer;
    }

    private void validateRow(QuestionExcelDTO row) {
        if (row.getType() == null) {
            throw new BusinessException("题型不能为空");
        }
        if (row.getContent() == null || row.getContent().isBlank()) {
            throw new BusinessException("题目内容不能为空");
        }
        if (row.getOptionA() == null || row.getOptionA().isBlank()) {
            throw new BusinessException("选项A不能为空");
        }
        if (row.getOptionB() == null || row.getOptionB().isBlank()) {
            throw new BusinessException("选项B不能为空");
        }
        if (row.getAnswer() == null || row.getAnswer().isBlank()) {
            throw new BusinessException("答案不能为空");
        }
        if (row.getDifficulty() == null) {
            throw new BusinessException("难度不能为空");
        }
    }

    private QuestionVO toQuestionVO(Question question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setType(question.getType());
        vo.setTypeName(QuestionTypeEnum.fromValue(question.getType()).getName());
        vo.setContent(question.getContent());
        vo.setImage(question.getImage());
        vo.setOptionA(question.getOptionA());
        vo.setOptionB(question.getOptionB());
        vo.setOptionC(question.getOptionC());
        vo.setOptionD(question.getOptionD());
        vo.setAnswer(question.getAnswer());
        vo.setExplanation(question.getExplanation());
        vo.setDifficulty(question.getDifficulty());
        vo.setDifficultyName(difficultyName(question.getDifficulty()));
        vo.setCategory(question.getCategory());
        vo.setAuditStatus(question.getAuditStatus());
        vo.setAuditStatusName(auditStatusName(question.getAuditStatus()));
        vo.setUseCount(question.getUseCount());
        vo.setCreateTime(question.getCreateTime());
        return vo;
    }

    private static String difficultyName(Integer difficulty) {
        if (difficulty == null) return "未知";
        return switch (difficulty) {
            case 1 -> "简单";
            case 2 -> "中等";
            case 3 -> "困难";
            default -> "未知";
        };
    }

    private static String auditStatusName(Integer auditStatus) {
        if (auditStatus == null) return "未知";
        return switch (auditStatus) {
            case 0 -> "待审核";
            case 1 -> "已审核";
            case 2 -> "已拒绝";
            default -> "未知";
        };
    }
}
