package org.example.zhikaoyunexamplatform.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

/**
 * 文件上传配置类
 *
 * 这个类用来配置文件上传的大小限制。
 *
 * 为什么需要这个配置？
 * Spring Boot 默认的文件上传最大大小只有 1MB。
 * 但是我们的项目有这样的场景：
 * 1. 教师通过 Excel 导入题库（.xlsx 文件可能有好几 MB）
 * 2. 用户上传头像图片
 * 3. 题目中可能包含图片
 * 默认的 1MB 不够用，上传时会出现 "FileSizeLimitExceededException" 异常。
 *
 * 配置说明：
 * - maxFileSize：单个文件的最大大小
 * - maxRequestSize：一次请求的总大小（可能同时上传多个文件）
 * 这里设置为 50MB，对 Excel 导入题库来说绰绰有余。
 * 如果以后需要调整，直接改这里的数字就行。
 *
 * 使用方法：
 * 这个类有 @Configuration 注解，Spring Boot 启动时会自动加载。
 * 不需要在代码中手动调用，也不需要修改 application.yaml。
 */
@Configuration
public class MultipartConfig {

    /**
     * 配置文件上传参数
     *
     * @Bean 表示这个方法返回的对象会被 Spring 容器管理。
     * MultipartConfigElement 是 Servlet 规范中用于配置文件上传的类。
     * Spring Boot 会自动使用这个配置覆盖默认的上传限制。
     *
     * @return 文件上传配置对象
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {

        // 创建 MultipartConfigFactory
        // 这个工厂类提供了方便的方法来设置上传参数
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 设置单个文件最大大小
        // DataSize.ofMegabytes(50) 表示 50 兆字节
        // 也可以用 ofKilobytes()、ofGigabytes() 等单位
        // 50MB 对 Excel 导入题库来说足够，一张 Excel 通常不超过 5MB
        factory.setMaxFileSize(DataSize.ofMegabytes(50));

        // 设置单次请求的总大小限制
        // 如果一个请求同时上传多个文件，所有文件加起来不能超过这个数
        // 同样设为 50MB，和单个文件限制保持一致
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));

        // 创建并返回配置对象
        return factory.createMultipartConfig();
    }
}
