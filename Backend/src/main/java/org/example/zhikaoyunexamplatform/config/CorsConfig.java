package org.example.zhikaoyunexamplatform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 将 /api/avatar/** 映射到项目根目录下的 avatar/ 文件夹
     * 头像文件访问：http://localhost:8080/api/avatar/xxx.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path avatarPath = resolveAvatarPath();
        String location = avatarPath.toUri().toString();
        // 确保以 / 结尾，Spring 要求资源目录必须以斜杠结尾
        if (!location.endsWith("/")) {
            location += "/";
        }
        log.info("头像资源目录: {} -> {}", avatarPath, location);
        registry.addResourceHandler("/api/avatar/**")
                .addResourceLocations(location);
    }

    /**
     * 智能解析 avatar 目录路径
     * 依次尝试 user.dir、user.dir 的父目录（兼容 IDE 工作目录不一致的情况）
     */
    private Path resolveAvatarPath() {
        // 1. 优先使用 user.dir/avatar
        Path candidate = Paths.get(System.getProperty("user.dir"), "avatar").toAbsolutePath().normalize();
        if (candidate.toFile().isDirectory()) {
            log.info("头像目录命中: user.dir = {}", System.getProperty("user.dir"));
            return candidate;
        }

        // 2. 回退：向上查找一级（IntelliJ 有时 user.dir 指向子模块）
        Path parent = candidate.getParent().getParent();
        if (parent != null) {
            Path fallback = parent.resolve("avatar").toAbsolutePath().normalize();
            if (fallback.toFile().isDirectory()) {
                log.warn("头像目录回退到: {}", fallback);
                return fallback;
            }
        }

        // 3. 最后手段：基于 classpath 推断项目根目录
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
