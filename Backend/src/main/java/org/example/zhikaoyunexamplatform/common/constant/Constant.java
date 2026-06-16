package org.example.zhikaoyunexamplatform.common.constant;

/**
 * 系统全局常量类
 *
 * 这个类定义了项目中到处会用到的常量值。
 * 为什么要定义常量而不是直接写数字/字符串？
 *
 * 1. 避免魔法数字
 *    代码里直接写 if (role == 0) 别人看不懂 0 是啥意思。
 *    写成 if (role == Constant.ROLE_STUDENT) 一眼就知道是"学生角色"。
 *
 * 2. 一处修改，处处生效
 *    如果角色值从 0/1/2 改成 1/2/3，只需要改这个文件。
 *    如果到处硬编码，改漏一个就出 bug。
 *
 * 3. 减少记忆负担
 *    新手不需要记住"学生角色是 0 还是 1"，
 *    直接写 Constant.ROLE_STUDENT 就行。
 *
 * 使用方法：
 *   // 判断角色
 *   if (role.equals(Constant.ROLE_TEACHER)) { ... }
 *
 *   // 逻辑删除查询
 *   WHERE is_deleted = Constant.DELETED_NO
 *
 *   // 状态判断
 *   if (status.equals(Constant.STATUS_ENABLE)) { ... }
 */
public class Constant {

    // 用户角色常量（建议统一使用 RoleEnum 替代）

    /**
     * @deprecated 请使用 {@link org.example.zhikaoyunexamplatform.common.enums.RoleEnum#STUDENT#getValue()}
     */
    @Deprecated
    public static final Integer ROLE_STUDENT = 0;

    /**
     * @deprecated 请使用 {@link org.example.zhikaoyunexamplatform.common.enums.RoleEnum#TEACHER#getValue()}
     */
    @Deprecated
    public static final Integer ROLE_TEACHER = 1;

    /**
     * @deprecated 请使用 {@link org.example.zhikaoyunexamplatform.common.enums.RoleEnum#ADMIN#getValue()}
     */
    @Deprecated
    public static final Integer ROLE_ADMIN = 2;

    // 逻辑删除常量

    /**
     * 未删除（正常状态）
     * 所有表的 is_deleted 字段，0 表示数据正常
     * SQL 查询时统一加 WHERE is_deleted = 0
     */
    public static final Integer DELETED_NO = 0;

    /**
     * 已删除（逻辑删除）
     * 所有表的 is_deleted 字段，1 表示数据已被删除
     * 删除操作统一 UPDATE 为 1
     */
    public static final Integer DELETED_YES = 1;

    // 启用状态常量

    /**
     * 禁用状态
     * t_user 表的 status 字段，0 表示账号被禁用
     * 被禁用的用户无法登录
     */
    public static final Integer STATUS_DISABLE = 0;

    /**
     * 正常状态
     * t_user 表的 status 字段，1 表示账号正常
     */
    public static final Integer STATUS_ENABLE = 1;

    // 考试状态常量

    /**
     * 考试未发布
     * t_exam 表的 status 字段，0 表示考试已创建但未发布
     * 教师可以修改、删除，学生看不到
     */
    public static final Integer EXAM_DRAFT = 0;

    /**
     * 考试进行中
     * t_exam 表的 status 字段，1 表示已发布，学生可以参加
     */
    public static final Integer EXAM_ONGOING = 1;

    /**
     * 考试已结束
     * t_exam 表的 status 字段，2 表示考试已结束
     * 学生无法再进入，教师可以在看统计结果
     */
    public static final Integer EXAM_FINISHED = 2;

    // 考试记录状态常量

    /**
     * 考试记录：进行中
     * t_exam_record 表的 status 字段，0 表示学生正在答题
     */
    public static final Integer RECORD_PROCESSING = 0;

    /**
     * 考试记录：已提交
     * t_exam_record 表的 status 字段，1 表示学生手动交卷
     */
    public static final Integer RECORD_SUBMITTED = 1;

    /**
     * 考试记录：超时自动交卷
     * t_exam_record 表的 status 字段，2 表示考试超时后自动提交
     */
    public static final Integer RECORD_TIMEOUT = 2;

    // 题目类型常量

    /**
     * 单选题
     * t_question 表的 type 字段，0 表示单选题
     * 答案格式：单个字母 A/B/C/D
     */
    public static final Integer QUESTION_SINGLE = 0;

    /**
     * 多选题
     * t_question 表的 type 字段，1 表示多选题
     * 答案格式：多个字母组合，按字母排序，如 "ABD"
     */
    public static final Integer QUESTION_MULTI = 1;

    /**
     * 判断题
     * t_question 表的 type 字段，2 表示判断题
     * 答案格式：A（正确）或 B（错误）
     */
    public static final Integer QUESTION_JUDGE = 2;

    // JWT 相关常量

    /**
     * Token 在请求头中的前缀
     * 格式：Authorization: Bearer <token>
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 请求头中存放 Token 的字段名
     */
    public static final String AUTH_HEADER = "Authorization";

    // 分页默认值

    /**
     * 默认页码（第一页）
     */
    public static final Integer PAGE_DEFAULT = 1;

    /**
     * 默认每页条数
     */
    public static final Integer SIZE_DEFAULT = 10;

    /**
     * 每页最大条数（防止恶意请求一次查太多数据）
     */
    public static final Integer SIZE_MAX = 100;

    // 通用标识

    /**
     * 通用"是/正确/正常"标识
     * 用于 answer_detail.is_correct、wrong_book.mastered 等
     */
    public static final Integer YES = 1;

    /**
     * 通用"否/错误/禁用"标识
     */
    public static final Integer NO = 0;

    /**
     * 私有构造方法，防止被实例化
     *
     * 工具类 / 常量类的最佳实践：
     * 把构造方法设为 private，别人就无法 new Constant() 了。
     * 因为这个类只有静态常量，不需要创建对象。
     * 如果有人写 new Constant() 说明他用错了。
     */
    private Constant() {
        throw new UnsupportedOperationException("常量类不能实例化");
    }
}
