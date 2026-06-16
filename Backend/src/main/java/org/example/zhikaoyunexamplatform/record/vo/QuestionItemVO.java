package org.example.zhikaoyunexamplatform.record.vo;

import lombok.Data;

@Data
public class QuestionItemVO {
    private Long questionId;
    private Integer type;
    private String content;
    private String image;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer score;
    private Integer sortOrder;
    private String studentAnswer;
}
