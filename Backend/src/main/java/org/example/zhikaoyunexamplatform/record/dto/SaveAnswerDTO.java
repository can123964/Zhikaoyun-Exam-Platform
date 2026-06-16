package org.example.zhikaoyunexamplatform.record.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveAnswerDTO {

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotNull(message = "答案不能为空")
    private String answer;
}
