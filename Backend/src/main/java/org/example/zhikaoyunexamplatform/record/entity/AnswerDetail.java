package org.example.zhikaoyunexamplatform.record.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerDetail {
    private Long id;
    private Long recordId;
    private Long questionId;
    private String userAnswer;
    private Integer isCorrect;
    private Integer score;
    private LocalDateTime answerTime;
    private String remark;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
