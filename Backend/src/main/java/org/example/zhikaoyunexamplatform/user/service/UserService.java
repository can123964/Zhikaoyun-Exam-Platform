package org.example.zhikaoyunexamplatform.user.service;

import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.user.dto.*;
import org.example.zhikaoyunexamplatform.user.entity.User;
import org.example.zhikaoyunexamplatform.user.vo.LoginVO;
import org.example.zhikaoyunexamplatform.user.vo.UserVO;

public interface UserService {

    /** 注册（学生自主注册，固定 role=0） */
    void register(UserRegisterDTO dto);

    /** 登录 */
    LoginVO login(LoginDTO dto);

    /** 获取当前用户信息 */
    UserVO getCurrentUser(Long userId);

    /** 修改密码 */
    void updatePassword(Long userId, UpdatePasswordDTO dto);

    /** 用户列表（管理员分页） */
    PageResult<UserVO> listUsers(Integer page, Integer size, Integer role, String keyword);

    /** 新增用户（管理员） */
    User createUser(UserCreateDTO dto);

    /** 修改用户（管理员） */
    void updateUser(Long id, UserUpdateDTO dto);

    /** 删除用户（管理员） */
    void deleteUser(Long id, Long currentUserId);

    /** 管理员重置用户密码（仅管理员） */
    void adminResetPassword(Long id, String newPassword);

    /** 用户修改个人信息（任意登录用户，只能改自己的） */
    void updateProfile(Long userId, ProfileUpdateDTO dto);

    /** 修改学生班级（教师/管理员，教师只能改自己班级的学生） */
    void updateStudentClass(Long userId, Long classId, Long currentUserId, Integer currentRole);

    /** 上传头像（任意登录用户，只能改自己的） */
    String uploadAvatar(Long userId, org.springframework.web.multipart.MultipartFile file);
}
