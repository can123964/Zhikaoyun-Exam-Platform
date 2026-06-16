package org.example.zhikaoyunexamplatform.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户修改个人信息请求 DTO
 *
 * 用户自行修改本人信息（手机号、邮箱）。
 * 与管理员修改（UserUpdateDTO）的区别：
 * - 不能修改 status（状态）和 remark（备注）
 * - 无需管理员权限，但只能改自己的
 */
@Data
public class ProfileUpdateDTO {

    /** 新的真实姓名（可选） */
    @Size(min = 1, max = 100, message = "真实姓名长度 1-100 个字符")
    private String realName;

    /** 新的手机号（可选） */
    @Size(max = 20, message = "手机号最长 20 个字符")
    private String phone;

    /** 新的邮箱（可选） */
    @Size(max = 100, message = "邮箱最长 100 个字符")
    private String email;
}
