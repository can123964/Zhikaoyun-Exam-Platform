package org.example.zhikaoyunexamplatform.user.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    /**
     * 新的真实姓名
     * 可选，传了就修改，不传保持原值。
     * 虽然可选，但一旦传了就不能是空串（min=1）。
     */
    @Size(min = 1, max = 100, message = "真实姓名长度 1-100 个字符")
    private String realName;

    /**
     * 新的班级ID
     * 可选，用于学生转班场景。
     */
    private Long classId;

    /**
     * 新的手机号
     * 可选。
     */
    @Size(max = 20, message = "手机号最长 20 个字符")
    private String phone;

    /**
     * 新的邮箱
     * 可选。
     */
    @Size(max = 100, message = "邮箱最长 100 个字符")
    private String email;

    /**
     * 新的状态：0-禁用 1-正常
     * 可选，管理员可以禁用或启用用户。
     */
    private Integer status;

    /**
     * 新的备注
     * 可选。
     */
    @Size(max = 500, message = "备注最长 500 个字符")
    private String remark;
}
