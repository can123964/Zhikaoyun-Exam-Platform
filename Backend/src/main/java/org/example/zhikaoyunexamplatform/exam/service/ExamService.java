package org.example.zhikaoyunexamplatform.exam.service;

import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.exam.dto.ExamCreateDTO;
import org.example.zhikaoyunexamplatform.exam.dto.ExamUpdateDTO;
import org.example.zhikaoyunexamplatform.exam.dto.RandomPaperDTO;
import org.example.zhikaoyunexamplatform.exam.vo.ExamQuestionVO;
import org.example.zhikaoyunexamplatform.exam.vo.ExamVO;

import java.util.List;

public interface ExamService {
    PageResult<ExamVO> listExams(Integer page, Integer size, Integer status, String keyword);
    ExamVO getExamById(Long id);
    ExamVO createExam(ExamCreateDTO dto, Long creatorId);
    ExamVO updateExam(Long id, ExamUpdateDTO dto);
    void deleteExam(Long id);
    void publishExam(Long id);
    void endExam(Long id);
    List<ExamQuestionVO> getExamQuestions(Long examId);
    void addQuestionToExam(Long examId, Long questionId, Integer score);
    void removeQuestionFromExam(Long examId, Long questionId);
    List<ExamQuestionVO> randomPaper(Long examId, RandomPaperDTO dto);
}
