package org.example.zhikaoyunexamplatform.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionDTO {

    @NotNull
    private Integer type;

    @NotBlank
    @Size(max = 2000)
    private String content;

    @Size(max = 500)
    private String image;

    @NotBlank
    @Size(max = 500)
    private String optionA;

    @NotBlank
    @Size(max = 500)
    private String optionB;

    @Size(max = 500)
    private String optionC;

    @Size(max = 500)
    private String optionD;

    @NotBlank
    private String answer;

    @Size(max = 2000)
    private String explanation;

    @NotNull
    private Integer difficulty;

    @Size(max = 100)
    private String category;
}
