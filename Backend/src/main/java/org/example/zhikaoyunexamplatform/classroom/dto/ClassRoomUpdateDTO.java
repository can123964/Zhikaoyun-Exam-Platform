package org.example.zhikaoyunexamplatform.classroom.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改班级时前端传过来的请求数据
 *
 * 和 ClassRoomCreateDTO 的关键区别：
 *   创建 DTO：必填字段有 @NotBlank，不能为空
 *   修改 DTO：所有字段都是可选（可 null）的
 *
 * 为什么修改时所有字段都是可选的？
 * 前端可能只想改"班级名称"，只传 className 就够了。
 * 如果修改 DTO 也有 @NotBlank，前端就必须传所有字段，
 * 否则校验不通过，体验很差。
 *
 * 那没传的字段怎么处理？
 * 没传的字段值是 null。在 Service 层的 updateById() 方法里，
 * XML Mapper 中的 <if test="字段 != null"> 会跳过 null 字段，
 * 所以这个字段在数据库中的值不会被修改（保持原值）。
 *
 * 这叫"部分更新"，也叫"增量更新"。
 * 相比之下，"全量更新"要求前端传所有字段，
 * 没传的字段会被设为 null，容易误清数据。
 * 部分更新更灵活也更安全。
 */
@Data
public class ClassRoomUpdateDTO {

    /**
     * 新的班级名称
     *
     * 只有 @Size 没有 @NotBlank，因为是可选字段。
     * 前端不传 className -> 不修改班级名称
     * 前端传 "计算机2203班" -> 修改为新名称
     * 前端传空字符串 "" -> @Size(min=1) 校验不通过，报错
     *
     * 关于 min=1：
     * 虽然字段可选，但一旦传了就不能是空串。
     * 这是为了防止前端恶意传空字符串把名称清空。
     */
    @Size(min = 1, max = 100, message = "班级名称长度 1-100 个字符")
    private String className;

    /**
     * 新的年级
     *
     * 同样是可选的，传了就修改，不传就不改。
     */
    @Size(min = 1, max = 20, message = "年级长度 1-20 个字符")
    private String grade;

    /**
     * 新的班主任 ID
     *
     * 传了就换班主任，不传就不换。
     * 如果要取消班主任（设为 null），前端传 teacherId: null 即可。
     *
     * 注意：Java 中"不传这个字段"和"传 null"都是 null，
     * 无法区分"不修改"和"设为空"。
     * 但目前的 XML 中 <if test="teacherId != null"> 只认 null，
     * 所以两种情况下都不会更新 teacher_id。
     * 如果后续需要"清空班主任"的功能，需要额外处理。
     */
    private Long teacherId;

    /**
     * 新的备注
     *
     * 传了就更新备注，不传就不改。
     */
    @Size(max = 500, message = "备注最长 500 个字符")
    private String remark;
}
