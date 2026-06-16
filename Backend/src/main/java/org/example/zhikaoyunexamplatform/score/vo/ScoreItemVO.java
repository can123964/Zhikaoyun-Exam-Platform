package org.example.zhikaoyunexamplatform.score.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScoreItemVO {
    private Long userId;
    private String username;
    private String realName;
    private String studentNo;
    private String className;
    private Integer score;
    private Integer totalScore;
    private Integer passScore;
    private Integer status;
    private String statusName;
    private Boolean isPassed;
    private LocalDateTime submitTime;
    private Integer duration;
}
