package org.example.zhikaoyunexamplatform.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求 DTO
 */
@Data
public class UpdatePasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度 6-50 个字符")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度 6-50 个字符")
    private String newPassword;
}
