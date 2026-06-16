package org.example.zhikaoyunexamplatform.question.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Question {
    private Long id;
    private Integer type;
    private String content;
    private String image;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String explanation;
    private Integer difficulty;
    private String category;
    private Integer useCount;
    private Integer auditStatus;
    private Long creatorId;
    private String remark;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
