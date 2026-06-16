package org.example.zhikaoyunexamplatform.question.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResultVO {

    private int successCount;
    private int failCount;
    private List<FailDetail> failDetails = new ArrayList<>();

    public void addSuccess() {
        successCount++;
    }

    public void addFail(int rowNum, String reason) {
        failCount++;
        failDetails.add(new FailDetail(rowNum, reason));
    }

    @Data
    @AllArgsConstructor
    public static class FailDetail {
        private int rowNum;
        private String reason;
    }
}
