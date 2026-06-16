package org.example.zhikaoyunexamplatform.record.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamRecordVO {
    private Long id;
    private Long examId;
    private String examTitle;
    private Integer duration;
    private Integer status;
    private String statusName;
    private Integer totalScore;
    private Integer passScore;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Long userId;
    private String username;
    private String realName;
    private String studentNo;
    private List<AnswerDetailVO> answerDetails;
}
