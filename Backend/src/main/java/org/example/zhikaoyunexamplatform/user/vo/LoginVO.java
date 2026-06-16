package org.example.zhikaoyunexamplatform.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录响应 VO
 */
@Data
@AllArgsConstructor
public class LoginVO {

    /** JWT Token */
    private String token;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 角色：0=学生，1=教师，2=管理员 */
    private Integer role;

    /** 头像 URL */
    private String avatar;
}
