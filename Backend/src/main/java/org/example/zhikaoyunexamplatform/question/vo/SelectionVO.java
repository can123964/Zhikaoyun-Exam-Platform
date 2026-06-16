package org.example.zhikaoyunexamplatform.question.vo;

import lombok.Data;

import java.util.List;

@Data
public class SelectionVO {

    private List<OptionItem> types;
    private List<OptionItem> difficulties;
    private List<String> categories;

    @Data
    public static class OptionItem {
        private Integer value;
        private String label;
    }
}
