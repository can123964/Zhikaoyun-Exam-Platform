package org.example.zhikaoyunexamplatform.exam.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.exam.entity.Exam;
import org.example.zhikaoyunexamplatform.exam.vo.ExamVO;

import java.util.List;

@Mapper
public interface ExamMapper {

    @Select("SELECT * FROM t_exam WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    Exam selectById(Long id);

    @Select("SELECT * FROM t_exam WHERE status = #{status} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " ORDER BY create_time DESC")
    List<Exam> selectByStatus(Integer status);

    @Select("SELECT * FROM t_exam WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " ORDER BY create_time DESC")
    List<Exam> selectAllExams();

    List<ExamVO> selectPage(@Param("status") Integer status, @Param("keyword") String keyword,
                            @Param("offset") int offset, @Param("size") int size,
                            @Param("creatorId") Long creatorId);

    int countByConditions(@Param("status") Integer status, @Param("keyword") String keyword,
                          @Param("creatorId") Long creatorId);

    @Insert("INSERT INTO t_exam (title, description, exam_type, creator_id, duration, start_time, end_time, " +
            "status, total_score, pass_score, show_answer, allow_retry, question_order, option_order, " +
            "max_count, max_tab_switches, remark, is_deleted, create_time, update_time) " +
            "VALUES (#{title}, #{description}, #{examType}, #{creatorId}, #{duration}, #{startTime}, #{endTime}, " +
            "#{status}, #{totalScore}, #{passScore}, #{showAnswer}, #{allowRetry}, #{questionOrder}, #{optionOrder}, " +
            "#{maxCount}, #{maxTabSwitches}, #{remark}, " + DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Exam exam);

    @Update("UPDATE t_exam SET title=#{title}, description=#{description}, duration=#{duration}, " +
            "start_time=#{startTime}, end_time=#{endTime}, total_score=#{totalScore}, pass_score=#{passScore}, " +
            "show_answer=#{showAnswer}, allow_retry=#{allowRetry}, question_order=#{questionOrder}, " +
            "option_order=#{optionOrder}, max_count=#{maxCount}, max_tab_switches=#{maxTabSwitches}, " +
            "remark=#{remark}, update_time=NOW() WHERE id=#{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void updateById(Exam exam);

    @Update("UPDATE t_exam SET status = #{status}, update_time = NOW() WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE t_exam SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT COUNT(*) FROM t_exam WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countAll();

    @Select("SELECT COUNT(*) FROM t_exam WHERE status = #{status} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByStatus(Integer status);

    @Select("SELECT COUNT(*) FROM t_exam WHERE creator_id = #{creatorId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByCreatorId(Long creatorId);

    @Select("SELECT COUNT(*) FROM t_exam WHERE creator_id = #{creatorId} AND status = #{status} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByCreatorIdAndStatus(@Param("creatorId") Long creatorId, @Param("status") Integer status);

    /** 批量根据 ID 列表查询考试（用于消除 N+1 查询） */
    List<Exam> selectByIds(@Param("ids") List<Long> ids);
}
