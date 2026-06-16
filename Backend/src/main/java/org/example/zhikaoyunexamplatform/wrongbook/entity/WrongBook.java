package org.example.zhikaoyunexamplatform.wrongbook.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WrongBook {
    private Long id;
    private Long userId;
    private Long questionId;
    private Long recordId;
    private String myAnswer;
    private Integer wrongCount;
    private Integer mastered;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
