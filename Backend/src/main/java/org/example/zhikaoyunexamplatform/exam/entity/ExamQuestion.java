package org.example.zhikaoyunexamplatform.exam.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamQuestion {
    private Long id;
    private Long examId;
    private Long questionId;
    private Integer score;
    private Integer sortOrder;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
