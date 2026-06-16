package org.example.zhikaoyunexamplatform.classroom.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.classroom.entity.ClassRoom;
import org.example.zhikaoyunexamplatform.classroom.vo.ClassRoomVO;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.common.enums.RoleEnum;

import java.util.List;

/**
 * 班级表 Mapper 接口
 *
 * Mapper 是 MyBatis 的核心概念，它定义了数据库操作的方法。
 * 每个方法对应一条 SQL 语句（写在注解里或者 XML 文件里）。
 * Spring 启动时，MyBatis 会自动为这个接口生成实现类，
 * 所以你不需要写 ClassRoomMapperImpl 这样的实现类。
 *
 * 这里的 SQL 写法有两种：
 * 1. 注解 SQL：简单 SQL 直接写在 @Select/@Insert/@Update/@Delete 注解里
 * 2. XML SQL：复杂 SQL（多表 JOIN、动态条件）写在 mapper/ClassRoomMapper.xml 里
 * 这种"注解+XML混用"的方式很灵活：简单的直接写在代码里方便查看，
 * 复杂的放到 XML 里避免 Java 代码里写长字符串。
 *
 * @Mapper 告诉 Spring 这个接口是 MyBatis 的 Mapper，
 * 需要自动扫描并生成实现类。
 * 即使不写 @Mapper 也可以，因为主启动类上有 @MapperScan 包扫描。
 * 但写上更清晰，一看就知道这是 Mapper 接口。
 */
@Mapper
public interface ClassRoomMapper {

    // CRUD 基础方法

    /**
     * 根据主键 ID 查询班级（未删除的）
     *
     * @Select 注解：里面写的就是 SQL 语句。
     * #{id} 是 MyBatis 的参数占位符，会自动替换为方法参数的值。
     * 和 JDBC 的 PreparedStatement 一样，可以防止 SQL 注入。
     *
     * WHERE is_deleted = 0：只查未删除的数据（逻辑删除过滤）。
     * 这是每个查询方法都要加的条件，切记！
     *
     * @param id 班级 ID
     * @return 班级对象，查不到返回 null
     */
    @Select("SELECT * FROM t_class WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    ClassRoom selectById(Long id);

    /**
     * 查询所有未删除的班级，按创建时间倒序排列
     *
     * ORDER BY create_time DESC：最新的在前面。
     * 列表查询一般都要排序，不然每次查出来的顺序不一样。
     *
     * @return 班级列表
     */
    @Select("SELECT * FROM t_class WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " ORDER BY create_time DESC")
    List<ClassRoom> selectList();

    /**
     * 新增班级
     *
     * 这里用了一段文本块（""" """），SQL 可以换行写，更清晰。
     * VALUES 中 is_deleted 固定为 0（新数据当然未删除），
     * create_time 和 update_time 用 NOW() 自动填充当前时间。
     *
     * @Options(useGeneratedKeys=true, keyProperty="id")：
     * 告诉 MyBatis 这条 INSERT 语句会由数据库自动生成主键，
     * 生成的主键会自动设置到传入对象的 id 属性上。
     * 这样插入后就能立即拿到新记录的 ID。
     *
     * @param classRoom 班级对象（id 为 null，由数据库生成）
     */
    @Insert("INSERT INTO t_class (class_name, grade, teacher_id, remark, is_deleted, create_time, update_time) " +
            "VALUES (#{className}, #{grade}, #{teacherId}, #{remark}, " + DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ClassRoom classRoom);

    /**
     * 根据主键更新班级（只更新非 null 字段）
     *
     * SQL 写在 ClassRoomMapper.xml 中，因为用了 <set> + <if> 动态 SQL。
     * 注解写动态 SQL 比较麻烦，放到 XML 里更合适。
     *
     * @param classRoom 班级对象，只有非 null 的字段会被更新
     */
    void updateById(ClassRoom classRoom);

    /**
     * 根据主键逻辑删除班级
     *
     * 注意：这是逻辑删除，不是物理删除。
     * 实际执行的是 UPDATE，把 is_deleted 设为 1。
     * 更新 update_time 记录删除时间。
     *
     * @param id 班级 ID
     */
    @Update("UPDATE t_class SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 统计同年级下同名班级的数量（用于新增时校验是否重复）
     *
     * 在创建班级时，需要检查同一年级下是否有重名的班级。
     * 如果有重复，就不能创建，提示用户。
     *
     * @param grade     年级
     * @param className 班级名称
     * @return 符合条件的记录数（0 表示没有重复，可以创建）
     */
    @Select("SELECT COUNT(*) FROM t_class WHERE grade = #{grade} AND class_name = #{className} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByGradeAndClassName(@Param("grade") String grade, @Param("className") String className);

    /**
     * 统计同年级下同名班级的数量（排除指定 ID，用于修改时校验是否重复）
     *
     * 在修改班级时，如果改了班级名称或年级，
     * 需要检查新名称是否和同年级的其他班级重复。
     * 排除自己（id != excludeId），不然自己和自己比肯定重复。
     *
     * @param grade     年级
     * @param className 班级名称
     * @param excludeId 需要排除的班级 ID（修改时传自己的 ID）
     * @return 符合条件的记录数（0 表示没有重复，可以修改）
     */
    @Select("SELECT COUNT(*) FROM t_class WHERE grade = #{grade} AND class_name = #{className} AND id != #{excludeId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByGradeAndClassNameExcludeId(@Param("grade") String grade, @Param("className") String className, @Param("excludeId") Long excludeId);

    // 自定义复杂查询

    /**
     * 查询班级列表（附带班主任姓名和学生人数）
     *
     * 这是一个复杂的多表 JOIN 查询，SQL 写在 ClassRoomMapper.xml 中。
     * 查询结果映射到 ClassRoomVO（因为包含了额外字段）。
     *
     * @return 班级 VO 列表（含班主任姓名、学生人数）
     */
    List<ClassRoomVO> selectClassRoomList(@Param("keyword") String keyword, @Param("grade") String grade);

    /**
     * 统计某个班级下的学生人数
     *
     * 用于删除班级前的校验：
     * 如果班级下还有学生，不能删除，需要提示用户先把学生转走。
     *
     * @param classId 班级 ID
     * @return 学生人数
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE class_id = #{classId} AND role = " + RoleEnum.STUDENT_VALUE + " AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    long countStudentsByClassId(Long classId);

    /**
     * 统计某个用户 ID 是否为教师角色
     *
     * 用于设置班主任时的校验：
     * 只能把教师（role=1）设为班主任。
     * 如果传的是学生或管理员 ID，校验不通过。
     *
     * @param teacherId 用户 ID
     * @return 该用户是教师角色的记录数（1 表示是教师，0 表示不是）
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE id = #{teacherId} AND role = " + RoleEnum.TEACHER_VALUE + " AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    long countTeacherById(Long teacherId);

    /** 查询某教师负责的班级列表 */
    List<ClassRoomVO> selectClassRoomListByTeacherId(@Param("teacherId") Long teacherId);

    /** 批量根据 ID 列表查询班级（用于消除 N+1 查询） */
    List<ClassRoom> selectByIds(@Param("ids") List<Long> ids);
}
