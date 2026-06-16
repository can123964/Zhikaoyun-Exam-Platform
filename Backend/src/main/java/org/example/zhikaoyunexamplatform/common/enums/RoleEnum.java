package org.example.zhikaoyunexamplatform.common.enums;

/**
 * 用户角色枚举
 */
public enum RoleEnum {

    UNKNOWN(-1, "未知", "ROLE_UNKNOWN"),
    STUDENT(0, "学生", "ROLE_STUDENT"),
    TEACHER(1, "教师", "ROLE_TEACHER"),
    ADMIN(2, "管理员", "ROLE_ADMIN");

    private final int value;
    private final String label;
    private final String authority;

    public static final int STUDENT_VALUE = 0;
    public static final int TEACHER_VALUE = 1;
    public static final int ADMIN_VALUE = 2;

    RoleEnum(int value, String label, String authority) {
        this.value = value;
        this.label = label;
        this.authority = authority;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public String getAuthority() {
        return authority;
    }

    public static RoleEnum fromValue(Integer value) {
        if (value == null) return null;
        for (RoleEnum role : values()) {
            if (role.value == value) return role;
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
