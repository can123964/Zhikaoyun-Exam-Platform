package org.example.zhikaoyunexamplatform.classroom.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级信息视图对象，返回给前端展示用
 *
 * VO 是 View Object（视图对象）的缩写。
 * 它的作用是"后端 -> 前端"返回数据。
 *
 * VO 和 Entity 的区别：
 * - Entity（实体类）：对应数据库表结构，包含所有字段
 * - VO（视图对象）：给前端展示用的，可以包含额外信息
 *
 * 这个 ClassRoomVO 比 ClassRoom（实体类）多了两个字段：
 * 1. teacherName：班主任的姓名（通过 JOIN 从 t_user 表查）
 * 2. studentCount：班级有多少学生（通过 COUNT 子查询计算）
 *
 * 为什么不在实体类里加这些字段？
 * 因为实体类应该和数据库表一一对应，
 * 多了 teacherName 和 studentCount 会破坏映射关系。
 * VO 就是用来放这些"额外查出来的展示字段"的。
 *
 * VO 比实体类少了一些前端不需要的字段：
 * - isDeleted：前端不需要知道逻辑删除状态
 * - updateTime：前端不需要知道更新时间
 * - teacherId：保留了这个，前端可能需要
 *
 * 这叫"接口隔离"：前端只需要它需要的数据，
 * 不需要的不要返回，减少数据传输量。
 */
@Data
public class ClassRoomVO {

    /**
     * 班级 ID
     * 直接映射 SQL 查询结果中的 id 字段。
     */
    private Long id;

    /**
     * 班级名称
     * 直接映射 SQL 查询结果中的 class_name 字段。
     */
    private String className;

    /**
     * 年级
     * 直接映射 SQL 查询结果中的 grade 字段。
     */
    private String grade;

    /**
     * 班主任的用户 ID
     * 保留这个字段，前端可能需要用它做跳转或其他操作。
     */
    private Long teacherId;

    /**
     * 班主任的姓名
     *
     * 这个字段在 t_class 表里不存在。
     * 是通过 LEFT JOIN t_user 表，用 teacher_id 查出来的。
     *
     * SQL 映射关系：
     *   teacher.real_name AS teacher_name -> teacherName
     *
     * 为什么用 LEFT JOIN 而不是 INNER JOIN？
     * LEFT JOIN：即使班级没有班主任（teacher_id 为 null），
     * 也能查到班级信息，teacherName 为 null 而已。
     * INNER JOIN：如果 teacher_id 为 null，整个班级都查不出来。
     */
    private String teacherName;

    /**
     * 该班级的学生人数
     *
     * 这个字段在 t_class 表里也不存在。
     * 是通过子查询从 t_user 表统计出来的：
     *   SELECT COUNT(*) FROM t_user WHERE class_id = c.id AND role = 0
     *
     * 为什么不直接把 studentCount 存在 t_class 表里？
     * 存起来叫"冗余字段"，查得快但维护麻烦（增删学生时要同步更新）。
     * 实时计算叫"动态统计"，数据绝对准确，但多一次查询。
     * 本项目学生数量不大（百级），选择实时计算，保证准确性。
     */
    private Integer studentCount;

    /**
     * 备注
     * 直接映射 SQL 查询结果中的 remark 字段。
     */
    private String remark;

    /**
     * 创建时间
     * 直接映射 SQL 查询结果中的 create_time 字段。
     * 前端可以用来排序，或者显示"这个班是什么时候建的"。
     */
    private LocalDateTime createTime;
}
