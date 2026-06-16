package org.example.zhikaoyunexamplatform.common.enums;

/**
 * 题目类型枚举
 */
public enum QuestionTypeEnum {

    SINGLE(0, "单选题"),
    MULTI(1, "多选题"),
    JUDGE(2, "判断题");

    private final int value;
    private final String name;

    public static final int SINGLE_VALUE = 0;
    public static final int MULTI_VALUE = 1;
    public static final int JUDGE_VALUE = 2;

    QuestionTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static QuestionTypeEnum fromValue(Integer value) {
        if (value == null) return null;
        for (QuestionTypeEnum type : values()) {
            if (type.value == value) return type;
        }
        throw new IllegalArgumentException("Unknown question type: " + value);
    }
}
