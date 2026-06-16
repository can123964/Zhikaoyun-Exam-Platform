package org.example.zhikaoyunexamplatform.wrongbook.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WrongBookVO {
    private Long id;
    private Long questionId;
    private Integer type;
    private String typeName;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String myAnswer;
    private String explanation;
    private Integer wrongCount;
    private Integer mastered;
    private LocalDateTime createTime;
}
