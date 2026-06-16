package org.example.zhikaoyunexamplatform.score.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RankVO {
    private Integer rank;
    private Long userId;
    private String realName;
    private String studentNo;
    private Integer score;
    private Integer status;
    private LocalDateTime submitTime;
}
