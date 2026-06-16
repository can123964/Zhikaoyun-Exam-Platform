package org.example.zhikaoyunexamplatform.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员创建用户时前端传过来的请求数据
 *
 * DTO 是 Data Transfer Object（数据传输对象）的缩写。
 * 它专门用来接收前端发来的 JSON 请求数据。
 *
 * 为什么不用实体类（User）直接接收前端数据？
 * 1. 实体类有 isDeleted、createTime 等不需要前端传的字段
 * 2. 实体类和前端表单的校验规则可能不同
 * 3. 前端只需要传一部分字段就够了
 *
 * 管理员可以指定角色（学生/教师/管理员），所以有 role 字段。
 * 如果是学生角色，Service 层会额外校验 studentNo 和 classId 是否填写。
 *
 * 注解校验流程：
 * 1. 前端发 POST 请求，JSON 数据到后端
 * 2. Controller 参数上有 @Valid 注解，触发校验
 * 3. Spring 自动检查每个字段是否符合注解规则
 * 4. 校验失败 -> 抛出 MethodArgumentNotValidException
 * 5. GlobalExceptionHandler 捕获后返回错误信息
 * 6. 校验通过 -> 继续执行业务代码
 */
@Data
public class UserCreateDTO {

    /**
     * 登录用户名，必填
     *
     * @NotBlank：不能为 null，不能是空字符串，不能只含空格。
     * @Size：长度限制在 2 到 50 个字符。
     * 数据库有唯一索引 uk_username，不能重复。
     * Service 层会额外查询数据库校验是否已存在。
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度 2-50 个字符")
    private String username;

    /**
     * 密码，必填
     *
     * 前端传明文密码过来，后端在 Service 层用 BCrypt 加密后再存入数据库。
     * 数据库中存的是加密后的密文，不会明文存储密码。
     * 长度限制 6-20 个字符，太短不安全，太长可能是恶意输入。
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度 6-20 个字符")
    private String password;

    /**
     * 真实姓名，必填
     *
     * 长度 1-100 个字符，不能为空。
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 1, max = 100, message = "真实姓名长度 1-100 个字符")
    private String realName;

    /**
     * 角色：0-学生 1-教师 2-管理员，必填
     *
     * 注意：Integer 类型用 @NotNull，不能用 @NotBlank。
     * @NotBlank 只能用在 String 类型上，用在 Integer 上会报错。
     * Service 层会额外校验值是否合法（只能是 0、1、2）。
     */
    @NotNull(message = "角色不能为空")
    private Integer role;

    /**
     * 学号，可选
     *
     * 只有学生角色才需要填学号，教师和管理员不需要。
     * 所以 DTO 层面设为可选，Service 层判断：
     * 如果角色是学生但没填学号，就抛异常提示必须填写。
     * 数据库有唯一索引 uk_student_no，不能重复。
     */
    @Size(max = 30, message = "学号最长 30 个字符")
    private String studentNo;

    /**
     * 班级ID，可选
     *
     * 只有学生角色才需要选班级。
     * 和 studentNo 一样，Service 层做业务校验。
     */
    private Long classId;

    /**
     * 手机号，可选
     *
     * 不是所有人都愿意提供手机号，所以设为可选。
     */
    @Size(max = 20, message = "手机号最长 20 个字符")
    private String phone;

    /**
     * 邮箱，可选
     *
     * 用于找回密码、发送通知等场景。
     * 管理员批量导入学生时，不是所有学生都有邮箱，所以设为可选。
     */
    @Size(max = 100, message = "邮箱最长 100 个字符")
    private String email;

    /**
     * 备注，可选
     *
     * 管理员可以写一些额外说明，比如"交换生，下学期结束"。
     */
    @Size(max = 500, message = "备注最长 500 个字符")
    private String remark;
}
