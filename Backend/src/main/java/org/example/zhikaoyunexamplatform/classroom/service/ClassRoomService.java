package org.example.zhikaoyunexamplatform.classroom.service;

import org.example.zhikaoyunexamplatform.classroom.dto.ClassRoomCreateDTO;
import org.example.zhikaoyunexamplatform.classroom.dto.ClassRoomUpdateDTO;
import org.example.zhikaoyunexamplatform.classroom.entity.ClassRoom;
import org.example.zhikaoyunexamplatform.classroom.vo.ClassRoomVO;
import org.example.zhikaoyunexamplatform.classroom.vo.ClassScoreVO;
import org.example.zhikaoyunexamplatform.user.vo.UserVO;
import java.util.List;

/**
 * 班级业务 Service 接口
 *
 * Service 层是业务逻辑层，位于 Controller 和 Mapper 之间。
 * 职责是：接收 Controller 传来的参数，处理业务逻辑，调用 Mapper 操作数据库。
 *
 * Service 接口 + 实现类的设计模式：
 * - 接口定义"做什么"（有哪些方法）
 * - 实现类定义"怎么做"（具体的业务逻辑）
 *
 * 为什么要有接口？
 * 1. 方便替换实现（比如测试时用 Mock 实现）
 * 2. 清晰的契约定义（调用方只需要看接口就知道有哪些功能）
 * 3. 实现类可以加 @Transactional 事务注解，接口保持干净
 *
 * 方法命名规范：
 * - listXxx：查询列表
 * - getXxx：查询单条
 * - createXxx：创建
 * - updateXxx：更新
 * - deleteXxx：删除
 */
public interface ClassRoomService {

    /**
     * 获取班级列表（含班主任姓名和学生人数）
     *
     * @param keyword 班级名称模糊搜索（可选）
     * @param grade   年级精确筛选（可选）
     * @return 班级 VO 列表
     */
    List<ClassRoomVO> listClassRooms(String keyword, String grade);

    /**
     * 根据 ID 获取班级详情
     *
     * @param id 班级 ID
     * @return 班级实体，如果不存在则抛异常
     */
    ClassRoom getById(Long id);

    /**
     * 创建班级
     *
     * @param req 前端传来的创建请求数据
     * @return 创建后的班级对象（含自增 ID）
     */
    ClassRoom createClassRoom(ClassRoomCreateDTO req);

    /**
     * 修改班级
     *
     * @param id  要修改的班级 ID
     * @param req 前端传来的修改请求数据
     */
    void updateClassRoom(Long id, ClassRoomUpdateDTO req);

    /**
     * 删除班级（逻辑删除）
     *
     * @param id 要删除的班级 ID
     */
    void deleteClassRoom(Long id);

    /**
     * 获取我负责的班级列表（教师用）
     *
     * @param userId 当前用户ID
     * @return 班级 VO 列表
     */
    List<ClassRoomVO> listMyClasses(Long userId);

    /**
     * 获取班级下的学生列表
     *
     * @param classId 班级 ID
     * @return 学生 VO 列表
     */
    List<UserVO> listStudentsByClassId(Long classId);

    List<ClassScoreVO> getClassScores(Long classId, Long examId);
}
