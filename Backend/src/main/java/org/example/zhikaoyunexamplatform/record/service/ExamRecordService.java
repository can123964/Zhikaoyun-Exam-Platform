package org.example.zhikaoyunexamplatform.record.service;

import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.record.dto.SaveAnswerDTO;
import org.example.zhikaoyunexamplatform.record.vo.*;

import java.util.List;
import java.util.Map;

public interface ExamRecordService {

    List<AvailableExamVO> getAvailableExams(Long userId);

    ExamPaperVO enterExam(Long userId, Long examId);

    void saveAnswer(Long userId, Long recordId, SaveAnswerDTO dto);

    Integer submitExam(Long userId, Long recordId);

    Map<String, Object> tabSwitch(Long userId, Long recordId);

    PageResult<ExamRecordVO> getMyRecords(Long userId, Integer page, Integer size);

    ExamRecordVO getRecordDetail(Long userId, Long recordId);

    PageResult<ExamRecordVO> getExamRecords(Long examId, Integer page, Integer size);

    ExamScoreStatVO getExamScoreStat(Long examId);

    PageResult<StudentScoreVO> getExamScoreRank(Long examId, Integer page, Integer size);

    void autoSubmitTimeoutRecords();
}
