package org.example.zhikaoyunexamplatform.question.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QuestionExcelDTO {

    @ExcelProperty(index = 0)
    private Integer type;

    @ExcelProperty(index = 1)
    private String content;

    @ExcelProperty(index = 2)
    private String image;

    @ExcelProperty(index = 3)
    private String optionA;

    @ExcelProperty(index = 4)
    private String optionB;

    @ExcelProperty(index = 5)
    private String optionC;

    @ExcelProperty(index = 6)
    private String optionD;

    @ExcelProperty(index = 7)
    private String answer;

    @ExcelProperty(index = 8)
    private Integer difficulty;

    @ExcelProperty(index = 9)
    private String explanation;

    @ExcelProperty(index = 10)
    private String category;
}
