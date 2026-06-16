package org.example.zhikaoyunexamplatform.common.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 地址工具类
 *
 * 这个类用来获取访问者的真实 IP 地址。
 *
 * 为什么要搞这么复杂？直接 request.getRemoteAddr() 不行吗？
 * 不行。因为项目部署的时候，通常会用 Nginx 做反向代理。
 * 用户在浏览器访问的是 Nginx，Nginx 再把请求转发给后端。
 * 这时后端拿到的 remoteAddr 是 Nginx 的 IP（比如 127.0.0.1），
 * 不是用户的真实 IP。
 *
 * 代理转发流程：
 *   用户 (1.2.3.4) → Nginx (127.0.0.1) → 后端
 *   如果没有这个工具类，后端以为访问者 IP 是 127.0.0.1
 *   用这个工具类，才能拿到用户的真实 IP 1.2.3.4
 *
 * 各代理服务器传递真实 IP 的方式：
 *   - Nginx：X-Forwarded-For 或 X-Real-IP
 *   - Apache：Proxy-Client-IP
 *   - WebLogic：WL-Proxy-Client-IP
 *   - 阿里云 SLB：X-Forwarded-For
 * 本工具类会依次检查这些头，直到拿到真实 IP。
 *
 * 哪里需要用到 IP？
 * 1. LogAspect 记录操作日志时，需要记录操作人 IP
 * 2. ExamRecord 记录考生 IP（防作弊：同一个 IP 短时间内大量提交？异常）
 * 3. 后台管理查看用户登录日志
 */
public class IpUtils {

    /**
     * 获取客户端真实 IP 地址
     *
     * 这个方法会依次尝试从各个代理头中获取真实 IP，
     * 如果都拿不到，最后 fallback 到 request.getRemoteAddr()。
     *
     * 使用示例：
     *   String ip = IpUtils.getClientIp(request);
     *   log.info("用户 IP 为：{}", ip);
     *
     * 在 Controller 中获取 IP 的标准写法：
     *   @GetMapping("/some")
     *   public Result<?> someMethod(HttpServletRequest request) {
     *       String ip = IpUtils.getClientIp(request);
     *       // ...
     *   }
     *
     * @param request HttpServletRequest 对象，由 Spring 自动注入
     * @return 客户端的真实 IP 地址，如果获取不到则返回 "unknown"
     */
    public static String getClientIp(HttpServletRequest request) {

        if (request == null) {
            return "unknown";
        }

        String ip = null;

        // 1. 检查 X-Forwarded-For 头
        // 这是最常用的代理头，Nginx、Apache、CDN 等都支持
        // 格式：X-Forwarded-For: client_ip, proxy1_ip, proxy2_ip
        // 如果有多个 IP（经过多层代理），第一个是客户端的真实 IP
        ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // 多个 IP 时取第一个
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }

        // 2. 检查 X-Real-IP 头
        // Nginx 专用头，比 X-Forwarded-For 更可靠（不会伪造）
        // 需要在 Nginx 配置中设置：proxy_set_header X-Real-IP $remote_addr;
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 3. 检查 Proxy-Client-IP 头
        // Apache HTTP Server 的代理头
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 4. 检查 WL-Proxy-Client-IP 头
        // Oracle WebLogic 服务器的代理头
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 5. 检查 HTTP_CLIENT_IP 头
        // 某些代理软件使用的头
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 6. 最后手段：直接从请求中获取
        // 如果没有代理，这个就是客户端的真实 IP
        // 如果经过代理，这个是代理服务器的 IP
        ip = request.getRemoteAddr();
        if (isValidIp(ip)) {
            return ip;
        }

        return "unknown";
    }

    /**
     * 判断 IP 字符串是否有效
     *
     * 有些代理头在没有代理的时候会是 "unknown" 字符串，
     * 或者在没设置的情况下为 null。
     * 这个方法用来过滤掉这些无效值。
     *
     * "valid" 是什么意思？
     * - 不为 null
     * - 不是空字符串
     * - 不等于 "unknown"（这是代理头没有设置时的默认值）
     *
     * @param ip IP 地址字符串
     * @return true 表示 IP 看起来有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}
