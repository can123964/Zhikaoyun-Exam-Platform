package org.example.zhikaoyunexamplatform.record.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvailableExamVO {
    private Long examId;
    private String title;
    private String description;
    private Integer duration;
    private Integer totalScore;
    private Integer passScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer allowRetry;
    private Integer questionCount;
    private Integer status;
    private Integer examState;
    private Integer userStatus;
}
