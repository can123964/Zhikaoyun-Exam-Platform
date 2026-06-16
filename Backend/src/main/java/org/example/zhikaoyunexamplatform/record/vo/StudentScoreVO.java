package org.example.zhikaoyunexamplatform.record.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentScoreVO {
    private Long userId;
    private String username;
    private String realName;
    private String studentNo;
    private String className;
    private Long recordId;
    private Integer score;
    private Integer status;
    private String statusName;
    private LocalDateTime submitTime;
}
