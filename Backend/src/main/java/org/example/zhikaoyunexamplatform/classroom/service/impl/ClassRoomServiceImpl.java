package org.example.zhikaoyunexamplatform.classroom.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.classroom.dto.ClassRoomCreateDTO;
import org.example.zhikaoyunexamplatform.classroom.dto.ClassRoomUpdateDTO;
import org.example.zhikaoyunexamplatform.classroom.entity.ClassRoom;
import org.example.zhikaoyunexamplatform.classroom.mapper.ClassRoomMapper;
import org.example.zhikaoyunexamplatform.classroom.service.ClassRoomService;
import org.example.zhikaoyunexamplatform.classroom.vo.ClassRoomVO;
import org.example.zhikaoyunexamplatform.classroom.vo.ClassScoreVO;
import org.example.zhikaoyunexamplatform.common.enums.RecordStatusEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 班级业务 Service 实现类
 *
 * 这个类实现了 ClassRoomService 接口中定义的所有方法。
 *
 * 各个注解的作用：
 * @Slf4j - Lombok 注解，自动生成 log 对象（log.info / log.warn / log.error）
 *          用这个代替 System.out.println，因为是标准日志框架，可以控制级别、输出到文件等
 * @Service - 标记这个类是 Service 层的实现，Spring 会自动管理它
 * @RequiredArgsConstructor - Lombok 注解，为所有 final 字段生成构造方法
 *          这里的 classRoomMapper 是 private final 的，Lombok 自动生成构造器注入
 * @Transactional(rollbackFor = Exception.class) - 类级别的事务注解
 *          类中所有方法都受事务管理，任何异常都回滚
 *          rollbackFor = Exception.class 表示所有异常都回滚（Spring 默认只回滚 RuntimeException）
 *
 * 三个关键注释的含义：
 * 1. @Slf4j - 记录日志
 * 2. @Service - Spring Bean
 * 3. @RequiredArgsConstructor - 构造器注入
 * 这是项目标准的三件套，每个 Service 实现类都要加。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ClassRoomServiceImpl implements ClassRoomService {

    /**
     * Mapper 对象，用于操作数据库
     *
     * private final 表示这个字段在构造时设置，之后不能修改。
     * 结合 @RequiredArgsConstructor，Spring 会自动把 ClassRoomMapper 注入进来。
     * 这叫"构造器注入"，比 @Autowired 字段注入更推荐：
     * - 在类创建时就需要，不会出现 NullPointerException
     * - 方便测试时传入 Mock 对象
     * - 明确依赖关系
     */
    private final ClassRoomMapper classRoomMapper;
    private final org.example.zhikaoyunexamplatform.user.mapper.UserMapper userMapper;
    private final org.example.zhikaoyunexamplatform.exam.mapper.ExamClassMapper examClassMapper;
    private final org.example.zhikaoyunexamplatform.exam.mapper.ExamMapper examMapper;
    private final org.example.zhikaoyunexamplatform.record.mapper.ExamRecordMapper examRecordMapper;

    /**
     * 获取班级列表
     *
     * 直接调用 Mapper 的 selectClassRoomList() 方法。
     * SQL 在 ClassRoomMapper.xml 中，是一个 LEFT JOIN 查询，
     * 会同时查出班主任姓名和学生人数。
     */
    @Override
    public List<ClassRoomVO> listClassRooms(String keyword, String grade) {
        return classRoomMapper.selectClassRoomList(keyword, grade);
    }

    @Override
    public List<ClassRoomVO> listMyClasses(Long userId) {
        return classRoomMapper.selectClassRoomListByTeacherId(userId);
    }

    /**
     * 根据 ID 获取班级
     *
     * 如果查不到数据，直接抛 BusinessException。
     * BusinessException 会被 GlobalExceptionHandler 捕获，
     * 自动返回错误响应给前端（code=400, msg="班级不存在"）。
     *
     * 这里有三个关键点：
     * 1. 查不到就抛异常，而不是返回 null
     * 2. 异常信息清晰（"班级不存在"），前端直接展示
     * 3. 调用方不需要处理 null 情况，逻辑更简单
     */
    @Override
    public ClassRoom getById(Long id) {
        ClassRoom classRoom = classRoomMapper.selectById(id);
        if (classRoom == null) {
            throw new BusinessException("班级不存在");
        }
        return classRoom;
    }

    /**
     * 创建班级
     *
     * 业务逻辑步骤：
     * 1. 校验班级名称在同一年级下是否唯一
     * 2. 如果传了班主任，校验该用户是否为教师角色
     * 3. 手动将 DTO 的值设置到 Entity 中
     * 4. 调用 Mapper 插入数据库
     * 5. 插入后 classRoom 对象中已经有数据库生成的自增 ID
     * 6. 记录操作日志
     *
     * 注意：这里没有用 BeanUtils.copyProperties 或 MapStruct，
     * 而是手动 setter。虽然代码多了几行，但更清晰：
     * - 一眼就能看出哪些字段被赋值了
     * - 不会出现字段名不匹配的问题
     * - 新手更容易理解
     */
    @Override
    public ClassRoom createClassRoom(ClassRoomCreateDTO req) {
        checkClassNameUnique(req.getGrade(), req.getClassName(), null);
        validateTeacherId(req.getTeacherId());

        ClassRoom classRoom = new ClassRoom();
        classRoom.setClassName(req.getClassName());
        classRoom.setGrade(req.getGrade());
        classRoom.setTeacherId(req.getTeacherId());
        classRoom.setRemark(req.getRemark());

        classRoomMapper.insert(classRoom);
        log.info("新增班级成功，id={}", classRoom.getId());
        return classRoom;
    }

    /**
     * 修改班级
     *
     * 业务逻辑步骤：
     * 1. 根据 ID 查询班级，不存在则抛异常
     * 2. 检查是否需要校验名称唯一性（只有改了年级或名称才校验）
     * 3. 如果改了班主任，校验新班主任是否为教师
     * 4. 创建一个只含修改字段的 Entity 对象
     * 5. 调用 Mapper 更新（XML 中的 <if> 只更新非 null 字段）
     *
     * 为什么创建新的 Entity 而不是从数据库查出来再修改？
     * 查出来的对象有很多字段，如果只更新部分字段，
     * 传给 Mapper 时，不想改的字段需要设为 null（麻烦且容易出错）。
     * 新创建一个只有 id 和需要修改字段的对象更干净。
     */
    @Override
    public void updateClassRoom(Long id, ClassRoomUpdateDTO req) {
        ClassRoom existing = getById(id);

        boolean gradeChanged = req.getGrade() != null && !req.getGrade().equals(existing.getGrade());
        boolean nameChanged = req.getClassName() != null && !req.getClassName().equals(existing.getClassName());
        if (gradeChanged || nameChanged) {
            String checkGrade = req.getGrade() != null ? req.getGrade() : existing.getGrade();
            String checkName = req.getClassName() != null ? req.getClassName() : existing.getClassName();
            checkClassNameUnique(checkGrade, checkName, id);
        }

        if (req.getTeacherId() != null && !req.getTeacherId().equals(existing.getTeacherId())) {
            validateTeacherId(req.getTeacherId());
        }

        ClassRoom updateEntity = new ClassRoom();
        updateEntity.setId(id);
        updateEntity.setClassName(req.getClassName());
        updateEntity.setGrade(req.getGrade());
        updateEntity.setTeacherId(req.getTeacherId());
        updateEntity.setRemark(req.getRemark());

        classRoomMapper.updateById(updateEntity);
        log.info("修改班级成功，id={}", id);
    }

    /**
     * 删除班级（逻辑删除）
     *
     * 业务逻辑步骤：
     * 1. 检查班级是否存在
     * 2. 检查班级下是否有学生，有则不能删除
     * 3. 执行逻辑删除（UPDATE is_deleted = 1）
     */
    @Override
    public void deleteClassRoom(Long id) {
        getById(id);

        long studentCount = classRoomMapper.countStudentsByClassId(id);
        if (studentCount > 0) {
            throw new BusinessException("班级下存在学生，无法删除");
        }

        classRoomMapper.deleteById(id);
        log.info("删除班级成功，id={}", id);
    }

    /**
     * 校验班级名称在同一年级下是否唯一
     *
     * 这是一个私有方法，只在当前类中使用。
     * 把校验逻辑抽成私有方法的好处：
     * 1. 代码复用（创建和修改都要校验唯一性）
     * 2. 方法名清晰表达了"在做什么"
     * 3. 主方法更简洁
     *
     * @param grade     年级
     * @param className 班级名称
     * @param excludeId 排除的班级 ID（修改时传，创建时传 null）
     */
    private void checkClassNameUnique(String grade, String className, Long excludeId) {
        int count;
        if (excludeId != null) {
            count = classRoomMapper.countByGradeAndClassNameExcludeId(grade, className, excludeId);
        } else {
            count = classRoomMapper.countByGradeAndClassName(grade, className);
        }
        if (count > 0) {
            throw new BusinessException("该年级下已存在同名班级");
        }
    }

    /**
     * 校验教师 ID 是否有效（必须是教师角色）
     *
     * 如果传了 null，直接返回（不校验）。
     * 如果传了非 null 值，查数据库确认这个用户是不是教师（role=1）。
     *
     * @param teacherId 用户 ID
     */
    private void validateTeacherId(Long teacherId) {
        if (teacherId == null) return;
        long count = classRoomMapper.countTeacherById(teacherId);
        if (count == 0) {
            throw new BusinessException("班主任必须是教师角色");
        }
    }

    // 班级学生列表

    @Override
    public List<org.example.zhikaoyunexamplatform.user.vo.UserVO> listStudentsByClassId(Long classId) {
        // 先校验班级存在
        getById(classId);

        // 查询该班级的学生（role=0 且未删除）
        List<org.example.zhikaoyunexamplatform.user.entity.User> students = userMapper.selectStudentsByClassId(classId);
        return students.stream()
                .map(org.example.zhikaoyunexamplatform.user.vo.UserVO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScoreVO> getClassScores(Long classId, Long examId) {
        getById(classId);

        List<Long> examIds;
        if (examId != null) {
            examIds = List.of(examId);
        } else {
            examIds = examClassMapper.selectExamIdsByClassId(classId);
        }

        if (examIds.isEmpty()) {
            return List.of();
        }

        List<org.example.zhikaoyunexamplatform.user.entity.User> students = userMapper.selectStudentsByClassId(classId);
        if (students.isEmpty()) {
            return List.of();
        }

        List<ClassScoreVO> result = new java.util.ArrayList<>();
        for (Long eid : examIds) {
            org.example.zhikaoyunexamplatform.exam.entity.Exam exam = examMapper.selectById(eid);
            if (exam == null) continue;

            for (org.example.zhikaoyunexamplatform.user.entity.User student : students) {
                List<org.example.zhikaoyunexamplatform.record.entity.ExamRecord> records =
                        examRecordMapper.selectByUserIdAndExamId(student.getId(), eid);
                org.example.zhikaoyunexamplatform.record.entity.ExamRecord submitted = records.stream()
                        .filter(r -> r.getStatus() == 1 || r.getStatus() == 2)
                        .findFirst().orElse(null);

                ClassScoreVO vo = new ClassScoreVO();
                vo.setUserId(student.getId());
                vo.setUsername(student.getUsername());
                vo.setRealName(student.getRealName());
                vo.setStudentNo(student.getStudentNo());
                vo.setExamId(eid);
                vo.setExamTitle(exam.getTitle());
                vo.setTotalScore(exam.getTotalScore());

                if (submitted != null) {
                    vo.setRecordId(submitted.getId());
                    vo.setScore(submitted.getScore());
                    vo.setStatus(submitted.getStatus());
                    vo.setStatusName(RecordStatusEnum.fromValue(submitted.getStatus()).getName());
                    vo.setSubmitTime(submitted.getSubmitTime());
                } else {
                    vo.setStatus(-1);
                    vo.setStatusName("未提交");
                }
                result.add(vo);
            }
        }
        return result;
    }
}
