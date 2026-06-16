package org.example.zhikaoyunexamplatform.user.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.common.enums.RoleEnum;
import org.example.zhikaoyunexamplatform.user.entity.User;

import java.util.List;

/**
 * 用户表 Mapper 接口
 *
 * 简单 SQL 用注解，复杂 SQL（动态条件/分页）写在 UserMapper.xml 中。
 */
@Mapper
public interface UserMapper {

    // 基础 CRUD

    @Select("SELECT * FROM t_user WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    User selectById(Long id);

    @Insert("INSERT INTO t_user (username, password, real_name, role, student_no, class_id, " +
            "phone, email, avatar, status, remark, is_deleted, create_time, update_time) " +
            "VALUES (#{username}, #{password}, #{realName}, #{role}, #{studentNo}, #{classId}, " +
            "#{phone}, #{email}, #{avatar}, #{status}, #{remark}, " +
            DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    /** 动态更新（只更新非 null 字段），SQL 写在 XML 中 */
    void updateById(User user);

    @Update("UPDATE t_user SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE id = #{id}")
    void deleteById(Long id);

    // 登录相关

    @Select("SELECT * FROM t_user WHERE username = #{username} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    User selectByUsername(String username);

    @Update("UPDATE t_user SET last_login_time = NOW(), login_count = login_count + 1 WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void updateLoginTime(Long id);

    // 密码修改

    @Update("UPDATE t_user SET password = #{password}, update_time = NOW() WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void updatePassword(@Param("id") Long id, @Param("password") String password);

    // 唯一性校验

    @Select("SELECT COUNT(*) FROM t_user WHERE username = #{username} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByUsername(String username);

    @Select("SELECT COUNT(*) FROM t_user WHERE student_no = #{studentNo} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByStudentNo(String studentNo);

    // 分页查询（XML 中实现）

    int countByConditions(@Param("role") Integer role, @Param("keyword") String keyword);

    List<User> selectPage(@Param("role") Integer role, @Param("keyword") String keyword,
                          @Param("offset") int offset, @Param("size") int size);

    // 班级学生查询

    @Select("SELECT * FROM t_user WHERE class_id = #{classId} AND role = " + RoleEnum.STUDENT_VALUE + " AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " ORDER BY student_no ASC")
    List<User> selectStudentsByClassId(Long classId);

    @Select("SELECT COUNT(*) FROM t_user WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countAll();

    @Select("SELECT COUNT(*) FROM t_user WHERE role = #{role} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByRole(Integer role);

    // 头像分配

    /** 查询所有用户（头像初始化用） */
    @Select("SELECT * FROM t_user WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<User> selectAll();

    /** 更新用户头像 */
    @Update("UPDATE t_user SET avatar = #{avatar}, update_time = NOW() WHERE id = #{id}")
    void updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    /** 统计某教师负责班级下的学生总数 */
    @Select("SELECT COUNT(*) FROM t_user WHERE role = " + RoleEnum.STUDENT_VALUE + " AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " AND class_id IN (SELECT id FROM t_class WHERE teacher_id = #{teacherId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + ")")
    int countStudentsByTeacherId(Long teacherId);

    /** 批量根据 ID 列表查询用户（用于消除 N+1 查询） */
    List<User> selectByIds(@Param("ids") List<Long> ids);

    /** 查询最近注册的用户（用于活动动态） */
    List<User> selectRecentRegistrations();
}
