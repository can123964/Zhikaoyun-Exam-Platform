package org.example.zhikaoyunexamplatform.question.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuestionVO {
    private Long id;
    private Integer type;
    private String typeName;
    private String content;
    private String image;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String explanation;
    private Integer difficulty;
    private String difficultyName;
    private String category;
    private Integer auditStatus;
    private String auditStatusName;
    private String creatorName;
    private Integer useCount;
    private LocalDateTime createTime;
}
