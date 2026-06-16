package org.example.zhikaoyunexamplatform.common.enums;

/**
 * 逻辑删除状态枚举
 * 对应数据库所有表的 is_deleted 字段：0 未删除，1 已删除
 *
 * 在 Java 代码中使用枚举：
 *   DeletedStatusEnum.NOT_DELETED.getValue()
 *
 * 在 Mapper SQL 注解中使用静态常量（编译时常量）：
 *   @Select("... AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
 */
public enum DeletedStatusEnum {

    NOT_DELETED(0),
    DELETED(1);

    public static final int NOT_DELETED_VALUE = 0;
    public static final int DELETED_VALUE = 1;

    private final int value;

    DeletedStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DeletedStatusEnum fromValue(Integer value) {
        if (value == null) return null;
        for (DeletedStatusEnum status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown deleted status: " + value);
    }
}
