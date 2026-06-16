package org.example.zhikaoyunexamplatform.common.utils;

import org.example.zhikaoyunexamplatform.common.enums.RoleEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全认证工具类 — 获取当前登录用户信息
 *
 * 这个类封装了从 Spring Security 中获取当前用户信息的重复代码。
 * 让新手队友不需要理解 SecurityContextHolder 是什么，直接调用就行。
 *
 * 为什么需要这个工具类？
 * 每次在 Controller/Service 中获取当前登录用户的信息，都要写：
 *   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 *   Long userId = (Long) auth.getPrincipal();
 * 又长又难记，新手很容易写错。
 *
 * 有了这个工具类，只需要：
 *   Long userId = SecurityUtils.getCurrentUserId();
 * 一行搞定。
 *
 * JWT 认证流程回顾：
 * 1. 用户登录 → 后端签发 JWT Token
 * 2. 后续请求携带 Authorization: Bearer xxx
 * 3. JwtAuthenticationFilter 解析 Token
 * 4. 将 userId 存入 SecurityContext（作为 principal）
 * 5. 将角色信息存入 authorities（如 ROLE_STUDENT）
 * 6. 后续代码通过 SecurityContextHolder 取用
 *
 * 所以 SecurityContextHolder 里存的是什么？
 * - principal = userId (Long 类型)
 * - authorities = [ROLE_STUDENT] 或 [ROLE_TEACHER] 或 [ROLE_ADMIN]
 * - credentials = null（JWT 无状态，不存密码）
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的 ID
     *
     * 这是最常用的方法，几乎每个 Controller 都需要。
     * 比如创建题目时需要知道是哪个教师创建的：
     *   Long userId = SecurityUtils.getCurrentUserId();
     *   question.setCreatorId(userId);
     *
     * @return 当前用户的 ID
     * @throws BusinessException 如果用户未登录，抛出 401 异常
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        throw new BusinessException(401, "用户未登录");
    }

    /**
     * 获取当前登录用户的 ID（不抛异常版本）
     *
     * 和 getCurrentUserId() 的区别：
     * 如果用户未登录，这个方法返回 null，而不是抛出异常。
     * 适用于某些"登录了就用用户信息，没登录也能用"的场景。
     *
     * 使用示例：
     *   Long userId = SecurityUtils.getCurrentUserIdQuietly();
     *   if (userId != null) {
     *       // 用户已登录的处理
     *   } else {
     *       // 用户未登录的处理
     *   }
     *
     * @return 当前用户的 ID，未登录时返回 null
     */
    public static Long getCurrentUserIdQuietly() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户的角色代码
     *
     * 返回角色数字代码：
     * - 0 = 学生 (STUDENT)
     * - 1 = 教师 (TEACHER)
     * - 2 = 管理员 (ADMIN)
     *
     * 注意：这个方法的实现是通过 authorities 中的角色字符串反向推导。
     * JwtAuthenticationFilter 中把 0/1/2 映射成了 ROLE_STUDENT/ROLE_TEACHER/ROLE_ADMIN。
     * 这里再解析回来。
     *
     * @return 角色代码（0/1/2），未登录时返回 null
     */
    public static Integer getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String roleStr = authority.getAuthority();
            for (RoleEnum roleEnum : RoleEnum.values()) {
                if (roleEnum.getAuthority().equals(roleStr)) {
                    return roleEnum.getValue();
                }
            }
            throw new BusinessException("未知角色: " + roleStr);
        }
        return null;
    }

    /**
     * 判断当前用户是否已登录
     *
     * 简单判断用户是否通过了 JWT 认证。
     *
     * @return true 表示已登录，false 表示未登录
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Long;
    }

    /**
     * 判断当前用户是否是管理员
     *
     * @return true 表示当前用户是管理员（role=2）
     */
    public static boolean isAdmin() {
        Integer role = getCurrentUserRole();
        return role != null && role == RoleEnum.ADMIN.getValue();
    }

    /**
     * 判断当前用户是否是教师
     *
     * @return true 表示当前用户是教师（role=1）
     */
    public static boolean isTeacher() {
        Integer role = getCurrentUserRole();
        return role != null && role == RoleEnum.TEACHER.getValue();
    }

    /**
     * 判断当前用户是否是学生
     *
     * @return true 表示当前用户是学生（role=0）
     */
    public static boolean isStudent() {
        Integer role = getCurrentUserRole();
        return role != null && role == RoleEnum.STUDENT.getValue();
    }
}
