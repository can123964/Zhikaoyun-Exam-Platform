package org.example.zhikaoyunexamplatform.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解（AOP 切面记录操作日志）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String value() default "";
}
