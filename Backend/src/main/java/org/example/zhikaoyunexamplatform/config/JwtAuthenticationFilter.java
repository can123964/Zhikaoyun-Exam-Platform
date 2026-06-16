package org.example.zhikaoyunexamplatform.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.common.enums.RoleEnum;
import org.example.zhikaoyunexamplatform.common.enums.UserStatusEnum;
import org.example.zhikaoyunexamplatform.common.utils.JwtUtils;
import org.example.zhikaoyunexamplatform.user.entity.User;
import org.example.zhikaoyunexamplatform.user.mapper.UserMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JWT 认证过滤器
 * 每次请求从 Authorization Header 提取 Token，验证通过后将用户信息注入 Spring Security 上下文。
 *
 * Controller 中取当前用户 ID 的方式：
 *   String token = request.getHeader("Authorization").substring(7);
 *   Long userId = jwtUtils.getUserId(token);
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    /** 用户状态缓存：userId -> true(正常) / false(禁用/不存在)，5分钟过期 */
    private final Cache<Long, Boolean> userStatusCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            // 只解析一次 Token，复用 Claims
            Claims claims = jwtUtils.parseClaims(token);
            Long userId = Long.parseLong(claims.getSubject());
            Integer role = claims.get("role", Integer.class);
            String username = claims.get("username", String.class);

            String roleStr;
            try {
                roleStr = RoleEnum.fromValue(role).getAuthority();
            } catch (IllegalArgumentException e) {
                roleStr = RoleEnum.UNKNOWN.getAuthority();
            }

            // 使用缓存避免每次请求查库，缓存5分钟过期
            Boolean cachedStatus = userStatusCache.get(userId, id -> {
                User user = userMapper.selectById(id);
                if (user == null) return false;
                Integer userStatus = user.getStatus();
                return userStatus != null && !userStatus.equals(UserStatusEnum.DISABLED.getValue());
            });

            if (Boolean.FALSE.equals(cachedStatus)) {
                log.warn("拦截无效Token：用户ID={} 不存在、已删除或已被封禁", userId);
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(roleStr))
                    );
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT 认证成功: userId={}, username={}, role={}", userId, username, roleStr);
        } catch (JwtException e) {
            log.warn("JWT 解析失败 [{}]: {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
