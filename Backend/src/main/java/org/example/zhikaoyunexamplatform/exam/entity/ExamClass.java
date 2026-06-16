package org.example.zhikaoyunexamplatform.exam.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamClass {
    private Long id;
    private Long examId;
    private Long classId;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
