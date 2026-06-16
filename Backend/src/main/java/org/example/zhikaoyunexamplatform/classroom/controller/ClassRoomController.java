package org.example.zhikaoyunexamplatform.classroom.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.classroom.dto.ClassRoomCreateDTO;
import org.example.zhikaoyunexamplatform.classroom.dto.ClassRoomUpdateDTO;
import org.example.zhikaoyunexamplatform.classroom.service.ClassRoomService;
import org.example.zhikaoyunexamplatform.classroom.vo.ClassRoomVO;
import org.example.zhikaoyunexamplatform.classroom.vo.ClassScoreVO;
import org.example.zhikaoyunexamplatform.common.annotation.Log;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.user.vo.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理 Controller（RESTful 接口）
 *
 * Controller 是后端 API 的入口，负责：
 * 1. 接收前端 HTTP 请求
 * 2. 校验请求参数
 * 3. 调用 Service 层处理业务
 * 4. 返回统一格式的响应
 *
 * 注解说明：
 * @RestController = @Controller + @ResponseBody
 *   表示这个类的所有方法都返回 JSON，不走视图解析器
 * @RequestMapping("/api/class") - 所有接口的统一前缀
 *   前端访问地址：http://localhost:8080/api/class/list
 * @RequiredArgsConstructor - 构造器注入（和 Service 实现类一样）
 *
 * 返回格式说明：
 * 所有方法都返回 Result<T> 类型。
 * Result.success(data) - 成功时返回数据
 * Result.success("消息") - 成功时只返回消息
 * 业务异常直接 throw BusinessException，由 GlobalExceptionHandler 处理。
 *
 * 权限说明：
 * @PreAuthorize 用于权限控制，@EnableMethodSecurity 已开启，注解正常生效。
 * hasRole('ADMIN') = 仅管理员可访问
 * hasAnyRole('TEACHER', 'ADMIN') = 教师/管理员可访问
 */
@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassRoomController {

    /**
     * Service 对象，处理具体的业务逻辑
     * 通过构造器注入（@RequiredArgsConstructor 自动生成构造方法）
     */
    private final ClassRoomService classRoomService;

    /**
     * 获取班级列表（所有角色可用）
     *
     * 请求方式：GET
     * 请求地址：/api/class/list
     * 认证要求：任意角色登录后即可访问
     *
     * @param keyword 班级名称模糊搜索（可选）
     * @param grade   年级精确筛选（可选）
     * @return 班级列表（含班主任姓名和学生人数）
     */
    @GetMapping("/list")
    public Result<List<ClassRoomVO>> listClassRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String grade) {
        return Result.success(classRoomService.listClassRooms(keyword, grade));
    }

    /**
     * 新增班级（仅管理员）
     *
     * 请求方式：POST
     * 请求地址：/api/class
     * 认证要求：仅管理员（role=2）
     *
     * @Valid 触发 DTO 中的 @NotBlank/@Size 校验
     * @RequestBody 将前端 JSON 自动转为 ClassRoomCreateDTO 对象
     *
     * @param req 创建班级的请求数据
     * @return 操作结果消息
     */
    @Log("新增班级")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createClassRoom(@Valid @RequestBody ClassRoomCreateDTO req) {
        classRoomService.createClassRoom(req);
        return Result.success("班级创建成功");
    }

    /**
     * 修改班级（仅管理员）
     *
     * 请求方式：PUT
     * 请求地址：/api/class/{id}
     * 认证要求：仅管理员
     *
     * @PathVariable Long id - 从 URL 路径中获取班级 ID，比如 /api/class/1
     * @param id  要修改的班级 ID
     * @param req 修改的请求数据（所有字段可选）
     * @return 操作结果消息
     */
    @Log("修改班级")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateClassRoom(@PathVariable Long id, @RequestBody ClassRoomUpdateDTO req) {
        classRoomService.updateClassRoom(id, req);
        return Result.success("班级修改成功");
    }

    /**
     * 删除班级（仅管理员）
     *
     * 请求方式：DELETE
     * 请求地址：/api/class/{id}
     * 认证要求：仅管理员
     *
     * 逻辑删除，不是物理删除。
     * 如果班级下还有学生，会返回错误提示。
     *
     * @param id 要删除的班级 ID
     * @return 操作结果消息
     */
    @Log("删除班级")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteClassRoom(@PathVariable Long id) {
        classRoomService.deleteClassRoom(id);
        return Result.success("班级删除成功");
    }

    /**
     * 获取当前教师负责的班级列表
     *
     * 请求方式：GET
     * 请求地址：/api/class/my
     * 认证要求：仅教师/管理员
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<List<ClassRoomVO>> getMyClasses() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(classRoomService.listMyClasses(userId));
    }

    /**
     * 获取班级学生列表（教师/管理员可用）
     *
     * 请求方式：GET
     * 请求地址：/api/class/{id}/students
     *
     * @param id 班级 ID
     * @return 学生列表
     */
    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<List<UserVO>> getClassStudents(@PathVariable Long id) {
        return Result.success(classRoomService.listStudentsByClassId(id));
    }

    /**
     * 获取班级成绩汇总（教师和管理员可用）
     *
     * 请求方式：GET
     * 请求地址：/api/class/{id}/scores?examId=xxx
     *
     * 注意：这个接口目前只校验了班级是否存在，
     * 实际成绩统计需要等 record 模块实现后才补全。
     *
     * @param id     班级 ID
     * @param examId 可选的考试 ID，不传则统计所有考试
     * @return 成绩统计（当前返回空列表占位）
     */
    @GetMapping("/{id}/scores")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<List<ClassScoreVO>> getClassScores(@PathVariable Long id,
                                          @RequestParam(required = false) Long examId) {
        return Result.success(classRoomService.getClassScores(id, examId));
    }
}
