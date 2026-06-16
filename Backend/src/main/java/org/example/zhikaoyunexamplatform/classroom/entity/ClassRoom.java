package org.example.zhikaoyunexamplatform.classroom.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级表 t_class 对应的实体类
 *
 * 什么是实体类？简单说，实体类就是一张数据库表的 Java 映射。
 * t_class 表里每一行数据，查询出来后就放在这个 ClassRoom 对象里。
 *
 * 数据库表字段和 Java 属性的对应关系（驼峰映射）：
 *   t_class.class_name  →  className
 *   t_class.teacher_id  →  teacherId
 *   t_class.create_time →  createTime
 * 原理：MyBatis 开启了 map-underscore-to-camel-case 配置，
 * 下划线命名的字段自动转成驼峰命名的属性。
 *
 * @Data 是 Lombok 注解，自动生成以下方法：
 * - getter：getId(), getClassName(), getGrade() ...
 * - setter：setId(), setClassName(), setGrade() ...
 * - toString()、equals()、hashCode()
 * 省去手动写这些重复代码的麻烦。
 *
 * 注意：这个类没有 @Table 或 @Entity 注解。
 * 因为 MyBatis 不需要 JPA 那一套注解。
 * MyBatis 通过 Mapper 接口里的 SQL 语句知道这个类和哪个表对应。
 */
@Data
public class ClassRoom {

    /**
     * 班级ID，主键，自增长
     *
     * 对应 t_class.id 字段，数据库类型 BIGINT。
     * 为什么用 Long 而不用 Integer？
     * 因为 BIGINT 是 64 位整数，Long 是 Java 的 64 位整数类型，完全匹配。
     * 用 Integer 的话，数据量大时可能不够用。
     * 企业项目主键统一用 Long 是标准做法。
     *
     * auto_increment 表示数据库自动生成 ID。
     * 插入数据时不用设置 id，数据库会自动分配。
     * 插入后 MyBatis 通过 @Options(useGeneratedKeys=true)
     * 可以把数据库生成的 ID 回填到这个字段。
     */
    private Long id;

    /**
     * 班级名称
     *
     * 对应 t_class.class_name 字段，VARCHAR(100) 类型。
     * 通常格式是"专业简称+入学年份+班号"，
     * 比如："计算机2201班"、"软件2202班"。
     * 在同一年级下（grade 相同），班级名称不能重复，
     * 数据库有唯一索引 uk_grade_class 保证。
     */
    private String className;

    /**
     * 年级
     *
     * 对应 t_class.grade 字段，VARCHAR(20) 类型。
     * 格式如："2022级"、"2023级"。
     * 用来区分不同入学年份的学生群体。
     */
    private String grade;

    /**
     * 班主任/负责教师的用户 ID
     *
     * 对应 t_class.teacher_id 字段，BIGINT 类型。
     * 这个字段存的是 t_user 表里某个教师的用户 ID。
     * 注意：这里只存 ID，不存教师姓名。
     * 为什么不存姓名？因为如果教师改名字了，这里也要改。
     * 存 ID 的话，查的时候 JOIN 一下 t_user 表就能拿到最新的名字。
     * 这叫"数据规范化"设计原则。
     *
     * 为什么用 Long 而不用 long？
     * Long 是包装类，可以为 null（表示没有班主任）。
     * long 是基本类型，不能为 null。
     * 因为班级可能没有班主任（teacher_id 字段允许为空），所以用 Long。
     */
    private Long teacherId;

    /**
     * 备注信息
     *
     * 对应 t_class.remark 字段，VARCHAR(500) 类型。
     * 可选的额外说明，比如：
     * "这个班是实验班，需要单独安排考试"
     * "周三下午有课，考试安排注意避开"
     * 传 null 或不传都可以。
     */
    private String remark;

    /**
     * 逻辑删除标记
     *
     * 对应 t_class.is_deleted 字段，TINYINT 类型。
     * 0 表示未删除（正常），1 表示已删除。
     *
     * 什么是逻辑删除？
     * 逻辑删除不是真的把数据从数据库里 DELETE 掉，
     * 而是把 is_deleted 设为 1，标记为已删除。
     * 以后查数据时统一加 WHERE is_deleted = 0 来过滤掉已删除的数据。
     *
     * 为什么不用物理删除？
     * 1. 误删了可以恢复（把 is_deleted 改回 0 即可）
     * 2. 保留历史数据，方便审计
     * 3. 企业项目标准做法
     *
     * 本项目所有业务表都有这个字段，设计统一。
     */
    private Integer isDeleted;

    /**
     * 创建时间
     *
     * 对应 t_class.create_time 字段，DATETIME 类型。
     * 这条数据是什么时候创建的。
     * 一旦创建就不再修改。
     *
     * 注意：这个字段不在 Java 代码里设置，
     * 而是在插入 SQL 中用 NOW() 函数自动填充。
     * LocalDateTime 是 Java 8 引入的时间类型，
     * 相比旧的 java.util.Date，它更安全、更好用。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     *
     * 对应 t_class.update_time 字段，DATETIME 类型。
     * 每次修改数据时，这个字段自动更新为当前时间。
     * 同样，在更新 SQL 中用 NOW() 手动设置，
     * MyBatis 不会自动维护这个字段。
     *
     * 每次更新操作都要记得在 SQL 里加 update_time = NOW()。
     */
    private LocalDateTime updateTime;
}
