package org.example.zhikaoyunexamplatform.record.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExamPaperVO {
    private Long recordId;
    private String examTitle;
    private Integer duration;
    private Long remainingSeconds;
    private Integer maxTabSwitches;
    private List<QuestionItemVO> questions;
}
