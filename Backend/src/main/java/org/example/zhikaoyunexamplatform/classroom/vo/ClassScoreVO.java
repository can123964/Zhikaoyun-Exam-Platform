package org.example.zhikaoyunexamplatform.classroom.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassScoreVO {
    private Long recordId;
    private Long userId;
    private String username;
    private String realName;
    private String studentNo;
    private Long examId;
    private String examTitle;
    private Integer score;
    private Integer totalScore;
    private Integer status;
    private String statusName;
    private LocalDateTime submitTime;
}
