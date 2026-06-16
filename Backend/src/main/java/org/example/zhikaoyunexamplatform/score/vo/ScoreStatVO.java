package org.example.zhikaoyunexamplatform.score.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ScoreStatVO {
    private Long examId;
    private String examTitle;
    private Double avgScore;
    private Integer maxScore;
    private Integer minScore;
    private Double passRate;
    private Integer submitCount;
    private Map<String, Integer> scoreDistribution;
}
