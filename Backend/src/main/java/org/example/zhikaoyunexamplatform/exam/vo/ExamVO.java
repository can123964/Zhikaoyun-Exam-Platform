package org.example.zhikaoyunexamplatform.exam.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamVO {
    private Long id;
    private String title;
    private String description;
    private Integer examType;
    private Long creatorId;
    private String creatorName;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String statusName;
    private Integer totalScore;
    private Integer passScore;
    private Integer showAnswer;
    private Integer allowRetry;
    private Integer questionOrder;
    private Integer optionOrder;
    private Integer maxCount;
    private Integer maxTabSwitches;
    private String remark;
    private LocalDateTime createTime;
    private List<Long> classIds;
    private List<String> classNames;
    private Integer questionCount;
    private Integer studentCount;
    private Integer submittedCount;
}
