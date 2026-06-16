package org.example.zhikaoyunexamplatform.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.annotation.Log;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.user.dto.*;
import org.example.zhikaoyunexamplatform.user.service.UserService;
import org.example.zhikaoyunexamplatform.user.vo.LoginVO;
import org.example.zhikaoyunexamplatform.user.vo.UserVO;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 注册（公开） */
    @Log("用户注册")
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }

    /** 登录（公开） */
    @Log("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 获取当前用户信息（任意登录用户） */
    @GetMapping("/info")
    public Result<UserVO> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(userService.getCurrentUser(userId));
    }

    /** 修改密码（任意登录用户） */
    @Log("修改密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updatePassword(userId, dto);
        return Result.success("密码修改成功");
    }

    /** 用户列表（管理员看全部，教师只看学生） */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.listUsers(page, size, role, keyword));
    }

    /** 新增用户（仅管理员） */
    @Log("新增用户")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createUser(@Valid @RequestBody UserCreateDTO dto) {
        userService.createUser(dto);
        return Result.success("用户创建成功");
    }

    /** 修改用户（仅管理员） */
    @Log("修改用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        userService.updateUser(id, dto);
        return Result.success("用户修改成功");
    }

    /** 删除用户（仅管理员） */
    @Log("删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteUser(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        userService.deleteUser(id, currentUserId);
        return Result.success("用户删除成功");
    }

    /** 管理员重置用户密码（仅管理员） */
    @Log("重置密码")
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> adminResetPassword(@PathVariable Long id,
                                              @Valid @RequestBody AdminResetPasswordDTO dto) {
        userService.adminResetPassword(id, dto.getNewPassword());
        return Result.success("密码重置成功");
    }

    /** 修改个人信息（任意登录用户，只能改自己的） */
    @Log("修改个人信息")
    @PutMapping("/profile")
    public Result<String> updateProfile(@Valid @RequestBody ProfileUpdateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateProfile(userId, dto);
        return Result.success("个人信息修改成功");
    }

    /** 修改学生班级（教师/管理员可用，教师只能修改自己班级的学生） */
    @Log("修改学生班级")
    @PutMapping("/{id}/class")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> updateStudentClass(@PathVariable Long id,
                                              @RequestBody Map<String, Long> body) {
        Long classId = body.get("classId");
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        userService.updateStudentClass(id, classId, currentUserId, currentRole);
        return Result.success("班级修改成功");
    }

    /** 上传头像（任意登录用户，只能上传自己的头像） */
    @Log("上传头像")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserId();
        String avatarUrl = userService.uploadAvatar(userId, file);
        return Result.success(avatarUrl);
    }
}
