package org.example.zhikaoyunexamplatform.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.zhikaoyunexamplatform.common.annotation.Log;
import org.example.zhikaoyunexamplatform.common.entity.SysLog;
import org.example.zhikaoyunexamplatform.common.mapper.SysLogMapper;
import org.example.zhikaoyunexamplatform.common.utils.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 * 拦截所有标注 @Log 注解的方法，自动记录操作人、请求方法、URL、参数、执行结果和耗时。
 *
 * 使用方式：在 Controller 方法上加 @Log("操作描述")
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;
    private final SysLogMapper sysLogMapper;
    private final JwtUtils jwtUtils;

    @Pointcut("@annotation(org.example.zhikaoyunexamplatform.common.annotation.Log)")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            try {
                saveLog(joinPoint, result, error, startTime);
            } catch (Exception e) {
                log.warn("记录操作日志失败: {}", e.getMessage());
            }
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Object result,
                         Throwable error, long startTime) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        if (logAnnotation == null) return;

        String operation = logAnnotation.value();

        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        String url = request.getRequestURI();
        String httpMethod = request.getMethod();
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String params = buildParams(joinPoint);

        int success = (error == null) ? 1 : 0;
        String errorMsg = (error != null) ? error.getMessage() : null;
        int responseTime = (int) (System.currentTimeMillis() - startTime);

        Long userId = null;
        String username = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            userId = (Long) auth.getPrincipal();
        }
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                username = jwtUtils.parseClaims(token).get("username", String.class);
            }
        } catch (Exception ignored) {
            log.warn("Log save failed", ignored);
        }

        log.info("【操作日志】操作={} 用户={} 方法={} URL={} 参数={} 结果={} 耗时={}ms IP={}",
                operation, userId, methodName, url, params,
                success == 1 ? "成功" : "失败(" + errorMsg + ")", responseTime, ip);

        try {
            SysLog sysLog = new SysLog();
            sysLog.setUserId(userId);
            sysLog.setUsername(username);
            sysLog.setOperation(operation);
            sysLog.setMethod(httpMethod);
            sysLog.setUrl(url);
            sysLog.setParams(params);
            sysLog.setResult(success);
            sysLog.setErrorMsg(errorMsg);
            sysLog.setIp(ip);
            sysLog.setUserAgent(userAgent);
            sysLog.setResponseTime(responseTime);
            sysLog.setCreateTime(LocalDateTime.now());
            
            // 异步写入日志，脱离主线程的 Spring 事务管理，防止业务报错时日志被连带回滚
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    sysLogMapper.insert(sysLog);
                } catch (Exception e) {
                    log.warn("异步写入操作日志到数据库失败: {}", e.getMessage());
                }
            });
        } catch (Exception e) {
            log.warn("构建操作日志对象失败: {}", e.getMessage());
        }
    }

    /** 构建参数字符串（过滤掉 HttpServletRequest/Response 等不可序列化的对象） */
    private String buildParams(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof HttpServletRequest
                        || args[i] instanceof jakarta.servlet.http.HttpServletResponse) {
                    continue;
                }
                if (sb.length() > 1) sb.append(", ");
                if (paramNames != null && i < paramNames.length) {
                    sb.append(paramNames[i]).append("=");
                }
                sb.append(objectMapper.writeValueAsString(args[i]));
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e) {
            return "序列化失败";
        }
    }

    /** 获取客户端真实 IP（兼容 Nginx 代理） */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
