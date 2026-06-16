package org.example.zhikaoyunexamplatform.exam.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RandomPaperDTO {

    @NotEmpty(message = "抽题规则不能为空")
    private @Valid List<RandomRule> rules;

    @Data
    public static class RandomRule {
        @NotNull(message = "题型不能为空")
        private Integer type;        // 0单选 1多选 2判断
        private Integer difficulty;  // 1简单 2中等 3困难（可选）
        private String  category;    // 分类（可选）
        @NotNull @Min(1)
        private Integer count;       // 抽取数量
        @NotNull @Min(1)
        private Integer score;       // 每题分值
    }
}
