package org.example.zhikaoyunexamplatform.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员重置用户密码请求 DTO
 */
@Data
public class AdminResetPasswordDTO {

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度 6-20 个字符")
    private String newPassword;
}
