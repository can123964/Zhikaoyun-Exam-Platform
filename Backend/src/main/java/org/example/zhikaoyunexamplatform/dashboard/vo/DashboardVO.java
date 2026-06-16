package org.example.zhikaoyunexamplatform.dashboard.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardVO {
    private Integer totalUsers;
    private Integer totalStudents;
    private Integer totalTeachers;
    private Integer totalQuestions;
    private Integer totalExams;
    private Integer ongoingExams;
    private Integer totalRecords;
    private Integer totalWrongBooks;

    /** 近 7 天考试趋势数据 */
    private List<TrendItem> examTrend;

    @Data
    public static class TrendItem {
        private String date;
        private Integer count;

        public TrendItem() {}

        public TrendItem(String date, Integer count) {
            this.date = date;
            this.count = count;
        }
    }
}
