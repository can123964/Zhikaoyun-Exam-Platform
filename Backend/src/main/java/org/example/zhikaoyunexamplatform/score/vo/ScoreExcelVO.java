package org.example.zhikaoyunexamplatform.score.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ScoreExcelVO {

    @ExcelProperty("姓名")
    private String realName;

    @ExcelProperty("学号")
    private String studentNo;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("得分")
    private Integer score;

    @ExcelProperty("总分")
    private Integer totalScore;

    @ExcelProperty("是否及格")
    private String isPassed;

    @ExcelProperty("提交时间")
    private String submitTime;

    @ExcelProperty("答题时长")
    private String durationStr;
}
