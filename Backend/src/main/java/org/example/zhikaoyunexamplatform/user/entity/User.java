package org.example.zhikaoyunexamplatform.user.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    /**
     * 用户Id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 班级Id
     */
    private Long classId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除 逻辑删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

}
