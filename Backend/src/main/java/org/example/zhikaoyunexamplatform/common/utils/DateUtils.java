package org.example.zhikaoyunexamplatform.common.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 日期时间工具类
 *
 * 这个类提供了一系列处理日期和时间的静态方法。
 * 项目里所有跟时间打交道的地方，都建议用这个工具类，
 * 而不是自己手写 SimpleDateFormat 或 Calendar。
 *
 * 为什么需要这个工具类？
 * 考试系统大量涉及时间操作：
 * - 判断考试是否超时：now - startTime > duration
 * - 格式化时间显示给前端：2026-06-03 14:30:00
 * - 解析前端传过来的时间字符串
 * - 计算两个时间之间差了多久
 * 如果每个地方都自己算，代码会又臭又长，还容易出错。
 *
 * 关于 Java 时间 API 的选择：
 * 本项目统一使用 java.time 包（Java 8+ 引入的新时间 API），
 * 不使用 java.util.Date、java.util.Calendar、SimpleDateFormat。
 * 原因：
 * - 新 API 是线程安全的（SimpleDateFormat 线程不安全）
 * - 代码更清晰（LocalDateTime 表示"本地日期时间"，含义明确）
 * - 操作更方便（直接 plus/minus/between）
 *
 * 所有方法都是静态的，直接 DateUtils.xxx() 调用即可，无需 new 对象。
 */
public class DateUtils {

    /**
     * 默认的日期时间格式：yyyy-MM-dd HH:mm:ss
     *
     * 这是项目里最常用的格式，比如：
     * - 前端传过来的时间字符串：2026-06-03 14:30:00
     * - 返回给前端的显示格式
     * - 日志中打印时间
     *
     * 为什么定义成常量？
     * 如果每个方法里都写一遍 "yyyy-MM-dd HH:mm:ss"，
     * 哪天要改成 "yyyy/MM/dd HH:mm:ss" 就得改几十个地方。
     * 定义成常量，改一处就行。
     */
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认的日期格式：yyyy-MM-dd
     * 用于只需要日期不含时间的场景，比如：2026-06-03
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 格式化 LocalDateTime 为字符串
     *
     * 把 LocalDateTime 对象转成 "2026-06-03 14:30:00" 这样的字符串。
     * 常用于返回给前端展示，或者写入日志。
     *
     * 使用示例：
     *   String timeStr = DateUtils.format(LocalDateTime.now());
     *   // 返回： "2026-06-03 14:30:00"
     *
     * @param dateTime 要格式化的日期时间对象
     * @return 格式化后的字符串，如果传入 null 则返回 null
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * 按指定格式格式化 LocalDateTime
     *
     * 如果不想用默认的 "yyyy-MM-dd HH:mm:ss" 格式，
     * 可以自己传格式，比如：
     *   DateUtils.format(LocalDateTime.now(), "yyyy/MM/dd")
     *   // 返回： "2026/06/03"
     *
     * @param dateTime 要格式化的日期时间对象
     * @param pattern  格式，比如 "yyyy-MM-dd"、"yyyy年MM月dd日"
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化 LocalDate 为字符串（不含时间）
     *
     * 如果只有日期没有时间，用这个方法。
     * 比如学生只选了生日（不需要几点几分）。
     *
     * 使用示例：
     *   String dateStr = DateUtils.formatDate(LocalDate.now());
     *   // 返回： "2026-06-03"
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的字符串，如 "2026-06-03"
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * 将字符串解析为 LocalDateTime
     *
     * 前端传来的时间字符串通常是 "2026-06-03 14:30:00"，
     * 需要转成 LocalDateTime 对象才能和数据库的时间做比较。
     *
     * 使用示例：
     *   LocalDateTime time = DateUtils.parse("2026-06-03 14:30:00");
     *   // time 可以调用 getHour()、isAfter() 等方法
     *
     * @param dateStr 时间字符串，如 "2026-06-03 14:30:00"
     * @return LocalDateTime 对象，如果传入 null 或空字符串则返回 null
     */
    public static LocalDateTime parse(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * 按指定格式将字符串解析为 LocalDateTime
     *
     * 如果前端传来的时间格式不是默认的，可以用这个方法指定格式。
     *
     * 使用示例：
     *   LocalDateTime time = DateUtils.parse("2026/06/03 14:30:00", "yyyy/MM/dd HH:mm:ss");
     *
     * @param dateStr 时间字符串
     * @param pattern 格式，和字符串匹配
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取当前时间的字符串表示
     *
     * 快速获取 "2026-06-03 14:30:00" 格式的当前时间。
     * 常用于日志记录、操作备注等场景。
     *
     * 使用示例：
     *   String now = DateUtils.nowStr();
     *   log.info("操作时间：{}", now);
     *
     * @return 当前时间的字符串，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * 获取当前时间，按指定格式返回字符串
     *
     * @param pattern 格式，如 "yyyyMMdd" 得到 "20260603"
     * @return 格式化后的当前时间字符串
     */
    public static String nowStr(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 计算两个时间的秒数差
     *
     * 考试系统最重要的方法之一！
     * 用于判断考试是否超时：
     *   long elapsed = DateUtils.betweenSeconds(record.getStartTime(), LocalDateTime.now());
     *   if (elapsed > exam.getDuration() * 60) {
     *       throw new BusinessException("考试已超时");
     *   }
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 相差的秒数（end - start），如果某个参数为 null 则返回 0
     */
    public static long betweenSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return Duration.between(start, end).getSeconds();
    }

    /**
     * 计算两个时间的分钟差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 相差的分钟数
     */
    public static long betweenMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return Duration.between(start, end).toMinutes();
    }

    /**
     * 判断一个考试记录是否已经超时
     *
     * 这是考试模块最核心的判断逻辑：
     * 学生开始答题到现在，如果超过了考试时限，就是超时。
     *
     * 使用示例（在 saveAnswer 和 submitExam 中校验）：
     *   if (DateUtils.isExpired(record.getStartTime(), exam.getDuration())) {
     *       throw new BusinessException("考试已超时，无法继续操作");
     *   }
     *
     * @param startTime      开始答题时间（t_exam_record.start_time）
     * @param durationMinutes 考试时限（t_exam.duration，单位分钟）
     * @return true 表示已超时，false 表示还在时限内
     */
    public static boolean isExpired(LocalDateTime startTime, int durationMinutes) {
        if (startTime == null) {
            return true;
        }
        return Duration.between(startTime, LocalDateTime.now())
                .toMinutes() >= durationMinutes;
    }

    /**
     * 计算某个时间点 + N 分钟后，和当前时间的剩余秒数
     *
     * 用于前端倒计时显示：
     *   还剩多少秒？
     *   long remaining = DateUtils.remainingSeconds(record.getStartTime(), exam.getDuration());
     *
     * @param startTime      开始时间
     * @param durationMinutes 总时长（分钟）
     * @return 剩余秒数，如果已超时返回 0
     */
    public static long remainingSeconds(LocalDateTime startTime, int durationMinutes) {
        if (startTime == null) {
            return 0L;
        }
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        long seconds = Duration.between(LocalDateTime.now(), endTime).getSeconds();
        return Math.max(0, seconds);
    }

    /**
     * 获取当天的开始时间（00:00:00）
     *
     * 比如今天是 2026-06-03 14:30:00，
     * 返回 2026-06-03 00:00:00
     *
     * 用于查询"今天的数据"时做范围起始值。
     *
     * @return 今天的 00:00:00
     */
    public static LocalDateTime getDayStart() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * 获取当天的结束时间（23:59:59.999999999）
     *
     * 比如今天是 2026-06-03 14:30:00，
     * 返回 2026-06-03 23:59:59.999999999
     *
     * 用于查询"今天的数据"时做范围结束值。
     *
     * @return 今天的 23:59:59
     */
    public static LocalDateTime getDayEnd() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }
}
