package org.example.zhikaoyunexamplatform.exam.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Exam {
    private Long id;
    private String title;
    private String description;
    private Integer examType;
    private Long creatorId;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private Integer totalScore;
    private Integer passScore;
    private Integer showAnswer;
    private Integer allowRetry;
    private Integer questionOrder;
    private Integer optionOrder;
    private Integer maxCount;
    private Integer maxTabSwitches;
    private String remark;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
