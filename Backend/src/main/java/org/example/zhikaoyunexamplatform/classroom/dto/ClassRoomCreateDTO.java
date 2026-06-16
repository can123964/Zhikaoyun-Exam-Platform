package org.example.zhikaoyunexamplatform.classroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建班级时前端传过来的请求数据
 *
 * DTO 是 Data Transfer Object（数据传输对象）的缩写。
 * 它专门用来接收前端发来的 JSON 请求数据。
 *
 * 为什么不用实体类（ClassRoom）直接接收前端数据？
 * 1. 实体类有 isDeleted、createTime 等不需要前端传的字段
 * 2. 实体类和前端表单的校验规则可能不同
 * 3. 前端只需要传一部分字段就够了
 *
 * 所以最佳实践是三层分离：
 * - DTO：接收前端请求数据
 * - Entity：对应数据库表结构
 * - VO：返回给前端展示的数据
 * 各层职责分明，互不干扰。
 *
 * 注解校验流程：
 * 1. 前端发 POST 请求，JSON 数据到后端
 * 2. Controller 参数上有 @Valid 注解，触发校验
 * 3. Spring 自动检查每个字段是否符合注解规则
 * 4. 校验失败 -> 抛出 MethodArgumentNotValidException
 * 5. GlobalExceptionHandler 捕获后返回 400 错误
 * 6. 校验通过 -> 继续执行业务代码
 * 这样就不用在代码里写一堆 if-else 做参数校验了。
 */
@Data
public class ClassRoomCreateDTO {

    /**
     * 班级名称
     *
     * @NotBlank 注解：不能为 null，不能是空字符串 ""，也不能是只含空格的字符串。
     * message 属性：校验失败时返回给前端的提示信息。
     * 比如前端没传 className，收到的错误就是 "班级名称不能为空"。
     *
     * @Size(min=1, max=100)：长度限制在 1 到 100 个字符。
     * 和 @NotBlank 的职责不同：
     * @NotBlank 确保"有值"，@Size 确保"长度合适"。
     * 两者可以同时用，各自负责不同的校验维度。
     */
    @NotBlank(message = "班级名称不能为空")
    @Size(min = 1, max = 100, message = "班级名称长度 1-100 个字符")
    private String className;

    /**
     * 年级
     *
     * 同样不能为空，长度 1-20。
     * 年级的常见格式："2022级"、"2023级"。
     */
    @NotBlank(message = "年级不能为空")
    @Size(min = 1, max = 20, message = "年级长度 1-20 个字符")
    private String grade;

    /**
     * 班主任的用户 ID
     *
     * 这个字段是可选（可 null）的。
     * 前端可以传 teacherId，也可以不传。
     * 如果传了，后端在 Service 层会校验这个 ID 是不是教师角色。
     *
     * 为什么不在 DTO 里校验 teacherId 是不是教师？
     * 因为需要查数据库才能知道，这种"业务逻辑校验"
     * 放在 Service 层做更合适。
     * DTO 只做"格式校验"（不为空、长度范围等），
     * Service 层做"业务校验"（ID 是否存在、角色是否正确）。
     */
    private Long teacherId;

    /**
     * 备注
     *
     * 可选字段，不加 @NotBlank。
     * 只加 @Size(max=500) 做长度限制。
     * @Size(max=500) 对 null 值是允许的，
     * 也就是前端不传备注不会报错。
     * 但如果传了，字符串长度不能超过 500 个字符。
     */
    @Size(max = 500, message = "备注最长 500 个字符")
    private String remark;
}
