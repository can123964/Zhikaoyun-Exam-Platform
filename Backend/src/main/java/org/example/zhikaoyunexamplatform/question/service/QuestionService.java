package org.example.zhikaoyunexamplatform.question.service;

import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.question.dto.QuestionDTO;
import org.example.zhikaoyunexamplatform.question.entity.Question;
import org.example.zhikaoyunexamplatform.question.vo.ImportResultVO;
import org.example.zhikaoyunexamplatform.question.vo.QuestionVO;
import org.example.zhikaoyunexamplatform.question.vo.SelectionVO;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {

    PageResult<QuestionVO> listQuestions(Integer page, Integer size, Integer type,
                                         Integer difficulty, String category,
                                         String keyword, Integer auditStatus,
                                         Long currentUserId, Integer currentRole);

    QuestionVO getQuestionById(Long id);

    Question createQuestion(QuestionDTO dto, Long creatorId);

    void updateQuestion(Long id, QuestionDTO dto, Long currentUserId, Integer currentRole);

    void deleteQuestion(Long id, Long currentUserId, Integer currentRole);

    ImportResultVO importExcel(MultipartFile file, Long creatorId);

    SelectionVO getSelections();
}
