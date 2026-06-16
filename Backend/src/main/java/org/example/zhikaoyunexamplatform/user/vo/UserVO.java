package org.example.zhikaoyunexamplatform.user.vo;

import lombok.Data;

import java.time.LocalDateTime;
import org.example.zhikaoyunexamplatform.common.enums.RoleEnum;
import org.example.zhikaoyunexamplatform.user.entity.User;

/**
 * 用户信息 VO（返回前端，不含密码等敏感字段）
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private Integer role;
    private String roleName;         // "学生" / "教师" / "管理员"（由 role 推导）
    private String studentNo;
    private Long classId;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private Integer loginCount;
    private LocalDateTime createTime;

    /**
     * 将 User 实体转换为统一的 UserVO 返回对象
     * 避免在各个 Service 中散落多套不同版本的转换逻辑
     */
    public static UserVO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        try {
            vo.setRoleName(RoleEnum.fromValue(user.getRole()).getLabel());
        } catch (IllegalArgumentException e) {
            vo.setRoleName("未知");
        }
        vo.setStudentNo(user.getStudentNo());
        vo.setClassId(user.getClassId());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLoginCount(user.getLoginCount());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
