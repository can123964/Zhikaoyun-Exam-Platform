package org.example.zhikaoyunexamplatform.record.vo;

import lombok.Data;

@Data
public class AnswerDetailVO {
    private Long questionId;
    private Integer type;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String studentAnswer;
    private Integer isCorrect;
    private Integer score;
    private String explanation;
}
