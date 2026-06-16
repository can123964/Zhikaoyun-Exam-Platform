package org.example.zhikaoyunexamplatform.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器工具类
 *
 * 这个类提供了"在非 Spring 管理的类中获取 Spring Bean"的能力。
 *
 * 什么时候需要这个东西？
 * 正常情况下，我们的 Service、Controller 都是 Spring 管理的，
 * 通过 @RequiredArgsConstructor 或 @Autowired 就能注入依赖。
 * 但有些特殊场景不行：
 *
 * 场景一：工具类/静态方法中需要调用 Service
 *   public class SomeUtil {
 *       public static void doSomething() {
 *           // ❌ 错误：工具类是静态的，不能 @Autowired
 *           UserService userService = SpringUtils.getBean(UserService.class);
 *           // ✅ 正确：通过 SpringUtils 获取
 *       }
 *   }
 *
 * 场景二：监听器/过滤器中需要调用业务方法
 *   某些 Filter 或 Listener 不是 Spring 管理的（或者生命周期不同步），
 *   这时候可以用 SpringUtils.getBean() 获取。
 *
 * 场景三：单元测试中快速获取 Bean
 *
 * 工作原理：
 * 这个类实现了 ApplicationContextAware 接口，
 * Spring 启动时会自动把 ApplicationContext 注入进来（setApplicationContext 方法）。
 * 然后我们把它存成静态变量，之后的静态方法就能用了。
 *
 * 注意：
 * 1. 这个类本身必须是 Spring 管理的（有 @Component 注解）
 * 2. 必须在 Spring 容器初始化完成后才能调用（不能在 @PostConstruct 之前调用）
 * 3. 本项目的 Controller/Service 中尽量用 @RequiredArgsConstructor 注入，
 *    只有上面说的特殊场景才用 SpringUtils
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    /**
     * Spring 应用上下文
     *
     * 静态变量，保存 Spring 容器引用。
     * 一旦 Spring 启动完成，这个变量就可以用了。
     * static 意味着所有地方都能通过 SpringUtils.applicationContext 访问。
     */
    private static ApplicationContext applicationContext;

    /**
     * Spring 容器启动时的回调方法
     *
     * 这个方法由 Spring 自动调用，
     * 参数 context 就是整个 Spring 容器的引用。
     * 我们把它存起来，供后续静态方法使用。
     *
     * @param context Spring 应用上下文
     * @throws BeansException Spring 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * 根据类型获取 Spring Bean
     *
     * 最常用的方法，按类型获取。
     *
     * 使用示例：
     *   UserService userService = SpringUtils.getBean(UserService.class);
     *   List<User> users = userService.listAll();
     *
     * @param clazz Bean 的类型（Class 对象）
     * @param <T>   Bean 的类型泛型
     * @return 该类型的 Bean 实例
     * @throws BeansException 如果容器中没有该类型的 Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取 Spring Bean
     *
     * 当容器中有同一个类型的多个 Bean 时，
     * 需要按名称获取。
     *
     * 使用示例：
     *   UserService userService = SpringUtils.getBean("userServiceImpl", UserService.class);
     *
     * @param name  Bean 的名称（通常是类名首字母小写，如 "userServiceImpl"）
     * @param clazz Bean 的类型
     * @param <T>   Bean 的类型泛型
     * @return 指定名称的 Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
