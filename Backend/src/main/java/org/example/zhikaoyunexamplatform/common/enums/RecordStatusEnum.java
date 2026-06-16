package org.example.zhikaoyunexamplatform.common.enums;

/**
 * 考试记录状态枚举
 * 对应 t_exam_record 表的 status 字段
 */
public enum RecordStatusEnum {

    PROCESSING(0, "进行中"),
    SUBMITTED(1, "已提交"),
    TIMEOUT(2, "超时自动交卷");

    private final int value;
    private final String name;

    public static final int PROCESSING_VALUE = 0;
    public static final int SUBMITTED_VALUE = 1;
    public static final int TIMEOUT_VALUE = 2;

    RecordStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static RecordStatusEnum fromValue(Integer value) {
        if (value == null) return null;
        for (RecordStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown record status: " + value);
    }
}
