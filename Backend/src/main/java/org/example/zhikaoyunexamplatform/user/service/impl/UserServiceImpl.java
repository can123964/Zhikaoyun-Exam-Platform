package org.example.zhikaoyunexamplatform.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;
import org.example.zhikaoyunexamplatform.common.enums.RoleEnum;
import org.example.zhikaoyunexamplatform.common.enums.UserStatusEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.utils.JwtUtils;
import org.example.zhikaoyunexamplatform.user.dto.*;
import org.example.zhikaoyunexamplatform.user.entity.User;
import org.example.zhikaoyunexamplatform.user.mapper.UserMapper;
import org.example.zhikaoyunexamplatform.classroom.mapper.ClassRoomMapper;
import org.example.zhikaoyunexamplatform.user.service.UserService;
import org.example.zhikaoyunexamplatform.user.vo.LoginVO;
import org.example.zhikaoyunexamplatform.user.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ClassRoomMapper classRoomMapper;

    /** 可用头像文件列表（启动时加载） */
    private static final List<String> AVATAR_LIST = new ArrayList<>();
    private static final Random RANDOM = new Random();
    private static final String AVATAR_URL_PREFIX = "/api/avatar/";

    @PostConstruct
    public void initAvatars() {
        File avatarDir = resolveAvatarDir().toFile();
        if (avatarDir.exists() && avatarDir.isDirectory()) {
            File[] files = avatarDir.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif");
            });
            if (files != null) {
                for (File f : files) {
                    AVATAR_LIST.add(AVATAR_URL_PREFIX + f.getName());
                }
            }
        }
        log.info("加载头像文件 {} 个", AVATAR_LIST.size());

        // 为已有用户分配头像（仅 avatar 为空的用户）
        List<User> allUsers = userMapper.selectAll();
        int assigned = 0;
        for (User user : allUsers) {
            if (user.getAvatar() == null || user.getAvatar().isBlank()) {
                userMapper.updateAvatar(user.getId(), pickRandomAvatar());
                assigned++;
            }
        }
        if (assigned > 0) {
            log.info("为 {} 个已有用户分配了默认头像", assigned);
        }
    }

    /** 随机选择一个头像URL */
    private String pickRandomAvatar() {
        if (AVATAR_LIST.isEmpty()) {
            return null;
        }
        return AVATAR_LIST.get(RANDOM.nextInt(AVATAR_LIST.size()));
    }

    // 注册

    @Override
    public void register(UserRegisterDTO dto) {
        // 校验用户名唯一性
        if (userMapper.countByUsername(dto.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 校验学号唯一性
        if (dto.getStudentNo() != null && userMapper.countByStudentNo(dto.getStudentNo()) > 0) {
            throw new BusinessException("学号已存在");
        }
        // 如果选了班级，校验班级存在
        if (dto.getClassId() != null && classRoomMapper.selectById(dto.getClassId()) == null) {
            throw new BusinessException("所选班级不存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setRole(RoleEnum.STUDENT.getValue()); // 注册固定为学生
        user.setStudentNo(dto.getStudentNo());
        user.setClassId(dto.getClassId()); // 可选班级
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(pickRandomAvatar());
        user.setStatus(UserStatusEnum.ENABLED.getValue());
        try {
            userMapper.insert(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("uk_username")) {
                throw new BusinessException("用户名已存在");
            } else if (msg != null && msg.contains("uk_student_no")) {
                throw new BusinessException("学号已存在");
            }
            throw new BusinessException("注册失败，请重试");
        }
        log.info("用户注册成功，id={}, classId={}", user.getId(), dto.getClassId());
    }

    // 登录

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == UserStatusEnum.DISABLED.getValue()) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        // 更新登录信息
        userMapper.updateLoginTime(user.getId());
        // 生成 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        log.info("用户登录成功，id={}, username={}", user.getId(), user.getUsername());
        return new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), user.getRole(), user.getAvatar());
    }

    // 获取当前用户

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return UserVO.fromEntity(user);
    }

    // 修改密码

    @Override
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 新旧密码不能相同
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        // 校验旧密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        // 加密新密码
        String encodedPwd = passwordEncoder.encode(dto.getNewPassword());
        userMapper.updatePassword(userId, encodedPwd);
        log.info("密码修改成功，userId={}", userId);
    }

    // 用户列表（分页）

    @Override
    public PageResult<UserVO> listUsers(Integer page, Integer size, Integer role, String keyword) {
        int total = userMapper.countByConditions(role, keyword);
        int offset = (page - 1) * size;
        List<User> userList = userMapper.selectPage(role, keyword, offset, size);
        List<UserVO> voList = userList.stream().map(UserVO::fromEntity).collect(Collectors.toList());
        return PageResult.of((long) total, page, size, voList);
    }

    // 新增用户（管理员）

    @Override
    public User createUser(UserCreateDTO dto) {
        // 校验用户名唯一性
        if (userMapper.countByUsername(dto.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 如果是学生，校验学号和班级
        if (dto.getRole() == RoleEnum.STUDENT.getValue()) {
            if (dto.getStudentNo() == null || dto.getStudentNo().isBlank()) {
                throw new BusinessException("学生必须填写学号");
            }
            if (userMapper.countByStudentNo(dto.getStudentNo()) > 0) {
                throw new BusinessException("学号已存在");
            }
            if (dto.getClassId() == null) {
                throw new BusinessException("学生必须选择班级");
            }
            if (classRoomMapper.selectById(dto.getClassId()) == null) {
                throw new BusinessException("分配的班级不存在");
            }
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setRole(dto.getRole());
        user.setStudentNo(dto.getStudentNo());
        user.setClassId(dto.getClassId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(pickRandomAvatar());
        user.setRemark(dto.getRemark());
        user.setStatus(UserStatusEnum.ENABLED.getValue());
        try {
            userMapper.insert(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("uk_username")) {
                throw new BusinessException("用户名已存在");
            } else if (msg != null && msg.contains("uk_student_no")) {
                throw new BusinessException("学号已存在");
            }
            throw new BusinessException("创建失败，请重试");
        }
        log.info("管理员创建用户成功，id={}, role={}", user.getId(), user.getRole());
        return user;
    }

    // 修改用户（管理员）

    @Override
    public void updateUser(Long id, UserUpdateDTO dto) {
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        User updateEntity = new User();
        updateEntity.setId(id);
        updateEntity.setRealName(dto.getRealName());
        
        if (dto.getClassId() != null) {
            if (classRoomMapper.selectById(dto.getClassId()) == null) {
                throw new BusinessException("分配的班级不存在");
            }
        }
        updateEntity.setClassId(dto.getClassId());
        updateEntity.setPhone(dto.getPhone());
        updateEntity.setEmail(dto.getEmail());
        updateEntity.setStatus(dto.getStatus());
        updateEntity.setRemark(dto.getRemark());
        userMapper.updateById(updateEntity);
        log.info("管理员修改用户成功，id={}", id);
    }

    // 删除用户（管理员）

    @Override
    public void deleteUser(Long id, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new BusinessException("不能删除当前登录用户");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userMapper.deleteById(id);
        log.info("管理员删除用户成功，id={}", id);
    }

    // 管理员重置密码

    @Override
    public void adminResetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        String encodedPwd = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(id, encodedPwd);
        log.info("管理员重置密码成功，userId={}", id);
    }

    // 修改学生班级

    @Override
    public void updateStudentClass(Long userId, Long classId, Long currentUserId, Integer currentRole) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!user.getRole().equals(RoleEnum.STUDENT.getValue())) {
            throw new BusinessException("只能修改学生的班级");
        }
        if (classId != null) {
            org.example.zhikaoyunexamplatform.classroom.entity.ClassRoom cls = classRoomMapper.selectById(classId);
            if (cls == null) {
                throw new BusinessException("班级不存在");
            }
            // 教师只能将学生分配到自己负责的班级
            if (!currentRole.equals(RoleEnum.ADMIN.getValue())
                    && !cls.getTeacherId().equals(currentUserId)) {
                throw new BusinessException("只能将学生分配到您自己负责的班级");
            }
        }
        User update = new User();
        update.setId(userId);
        update.setClassId(classId);
        userMapper.updateById(update);
        log.info("修改学生班级成功，userId={}, classId={}", userId, classId);
    }

    // 用户修改个人信息

    @Override
    public void updateProfile(Long userId, ProfileUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        User updateEntity = new User();
        updateEntity.setId(userId);
        updateEntity.setRealName(dto.getRealName());
        updateEntity.setPhone(dto.getPhone());
        updateEntity.setEmail(dto.getEmail());
        userMapper.updateById(updateEntity);
        log.info("用户修改个人信息成功，userId={}", userId);
    }

    // 私有辅助方法

    @Override
    public String uploadAvatar(Long userId, org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        if (!ext.matches("\\.(jpg|jpeg|png|gif)")) {
            throw new BusinessException("只支持 jpg、jpeg、png、gif 格式的图片");
        }

        // 校验文件大小（限制 2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("头像图片不能超过 2MB");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 生成唯一文件名并保存
        String filename = userId + "_" + System.currentTimeMillis() + ext;
        Path avatarDir = resolveAvatarDir();
        try {
            Files.createDirectories(avatarDir);
            Path targetPath = avatarDir.resolve(filename).normalize();
            // 安全校验：确保目标路径在 avatar 目录内，防止路径穿越
            if (!targetPath.startsWith(avatarDir)) {
                throw new BusinessException("非法文件路径");
            }
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            log.error("头像上传失败：userId={}, error={}", userId, e.getMessage());
            throw new BusinessException("头像保存失败，请重试");
        }

        // 更新数据库
        String avatarUrl = AVATAR_URL_PREFIX + filename;
        userMapper.updateAvatar(userId, avatarUrl);
        log.info("头像上传成功，userId={}, url={}", userId, avatarUrl);
        return avatarUrl;
    }

    /**
     * 解析 avatar 目录路径
     * 依次尝试 user.dir、user.dir 父目录、classpath 推断
     */
    private Path resolveAvatarDir() {
        // 1. 优先使用 user.dir/avatar
        Path candidate = Paths.get(System.getProperty("user.dir"), "avatar").toAbsolutePath().normalize();
        if (candidate.toFile().isDirectory()) {
            return candidate;
        }

        // 2. 回退：向上查找一级
        Path parent = candidate.getParent().getParent();
        if (parent != null) {
            Path fallback = parent.resolve("avatar").toAbsolutePath().normalize();
            if (fallback.toFile().isDirectory()) {
                log.warn("头像目录回退到: {}", fallback);
                return fallback;
            }
        }

        // 3. 最后手段：基于 classpath 推断
        String classPath = System.getProperty("java.class.path", "");
        int targetIdx = classPath.indexOf("target" + File.separator + "classes");
        if (targetIdx > 0) {
            String beforeTarget = classPath.substring(0, targetIdx);
            Path fromClasspath = Paths.get(beforeTarget, "avatar").toAbsolutePath().normalize();
            if (fromClasspath.toFile().isDirectory()) {
                log.warn("头像目录从 classpath 推断: {}", fromClasspath);
                return fromClasspath;
            }
        }

        log.error("未找到头像目录! user.dir={}, 尝试路径: {}", System.getProperty("user.dir"), candidate);
        return candidate;
    }

}
