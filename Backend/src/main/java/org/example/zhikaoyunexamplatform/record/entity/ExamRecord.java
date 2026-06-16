package org.example.zhikaoyunexamplatform.record.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamRecord {
    private Long id;
    private Long examId;
    private Long userId;
    private Integer score;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Integer duration;
    private Integer remainingSeconds;
    private String ip;
    private String userAgent;
    private Integer version;
    private Integer tabSwitchCount;
    private String remark;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
