package org.example.zhikaoyunexamplatform.exam.vo;

import lombok.Data;

@Data
public class ExamQuestionVO {
    private Long id;
    private Long questionId;
    private Integer score;
    private Integer sortOrder;
    private Integer type;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String explanation;
}
