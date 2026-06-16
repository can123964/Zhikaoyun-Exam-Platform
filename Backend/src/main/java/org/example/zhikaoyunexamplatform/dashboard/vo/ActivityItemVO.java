package org.example.zhikaoyunexamplatform.dashboard.vo;

import lombok.Data;

@Data
public class ActivityItemVO {
    /** exam_submit / user_register */
    private String type;
    private String title;
    private String subtitle;
    private String time;
}
