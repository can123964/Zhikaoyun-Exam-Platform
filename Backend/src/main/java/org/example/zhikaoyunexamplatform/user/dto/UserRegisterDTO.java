package org.example.zhikaoyunexamplatform.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户自主注册时前端传过来的请求数据
 *
 * 和 UserCreateDTO 的区别：
 * 1. 没有 role 字段 —— 注册固定为学生角色，用户不能自己选角色
 * 2. studentNo 是必填的 —— 注册的都是学生，必须有学号
 * 3. classId 可选 —— 注册时可以选班级，也可后续由教师分配
 * 4. 没有 remark —— 学生自己注册不需要写备注
 */
@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度 2-50 个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度 6-20 个字符")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 1, max = 100, message = "真实姓名长度 1-100 个字符")
    private String realName;

    @NotBlank(message = "学号不能为空")
    @Size(max = 30, message = "学号最长 30 个字符")
    private String studentNo;

    @Size(max = 20, message = "手机号最长 20 个字符")
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    @Size(max = 100, message = "邮箱最长 100 个字符")
    private String email;

    /** 注册时选择的班级ID（可选，教师也可后续分配） */
    private Long classId;
}
