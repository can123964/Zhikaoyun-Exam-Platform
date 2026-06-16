package org.example.zhikaoyunexamplatform.common.enums;

// 用户状态枚举
// 对应数据库 t_user 表的 status 字段：0 禁用，1 正常
public enum UserStatusEnum {

    DISABLED(0),
    ENABLED(1);

    private final int value;

    UserStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatusEnum fromValue(Integer value) {
        if (value == null) return null;
        for (UserStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown user status: " + value);
    }
}
