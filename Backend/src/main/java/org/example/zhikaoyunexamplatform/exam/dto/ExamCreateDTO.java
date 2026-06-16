package org.example.zhikaoyunexamplatform.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamCreateDTO {
    @NotBlank(message = "考试名称不能为空")
    private String title;
    private String description;
    @NotNull(message = "考试类型不能为空")
    private Integer examType;
    @NotNull(message = "考试时长不能为空")
    private Integer duration;
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Integer totalScore = 100;
    private Integer passScore = 60;
    private Integer showAnswer = 0;
    private Integer allowRetry = 0;
    private Integer questionOrder = 0;
    private Integer optionOrder = 0;
    private Integer maxCount;
    private Integer maxTabSwitches = 3;
    private String remark;
    private List<Long> classIds;
}
