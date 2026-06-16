package org.example.zhikaoyunexamplatform.common.enums;

/**
 * 考试状态枚举
 */
public enum ExamStatusEnum {

    DRAFT(0, "未发布"),
    ONGOING(1, "进行中"),
    FINISHED(2, "已结束");

    private final int value;
    private final String name;

    public static final int DRAFT_VALUE = 0;
    public static final int ONGOING_VALUE = 1;
    public static final int FINISHED_VALUE = 2;

    ExamStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static ExamStatusEnum fromValue(Integer value) {
        if (value == null) return null;
        for (ExamStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown exam status: " + value);
    }
}
