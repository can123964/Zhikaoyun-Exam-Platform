package org.example.zhikaoyunexamplatform.record.vo;

import lombok.Data;

@Data
public class ExamScoreStatVO {
    private Long examId;
    private String examTitle;
    private Integer totalStudents;
    private Integer submittedCount;
    private Double avgScore;
    private Integer maxScore;
    private Integer minScore;
    private Double passRate;
}
