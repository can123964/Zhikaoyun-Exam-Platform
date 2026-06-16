package org.example.zhikaoyunexamplatform.record.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.record.entity.ExamRecord;

import java.util.List;

@Mapper
public interface ExamRecordMapper {

    @Insert("INSERT INTO t_exam_record (exam_id, user_id, score, status, start_time, remaining_seconds, version, is_deleted, create_time, update_time) " +
            "VALUES (#{examId}, #{userId}, #{score}, #{status}, #{startTime}, #{remainingSeconds}, #{version}, " +
            DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ExamRecord record);

    @Select("SELECT * FROM t_exam_record WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    ExamRecord selectById(Long id);

    @Select("SELECT * FROM t_exam_record WHERE user_id = #{userId} AND exam_id = #{examId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<ExamRecord> selectByUserIdAndExamId(@Param("userId") Long userId, @Param("examId") Long examId);

    @Select("SELECT * FROM t_exam_record WHERE user_id = #{userId} AND status = #{status} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<ExamRecord> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    int countByUserId(@Param("userId") Long userId);

    List<ExamRecord> selectPageByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    int countByExamId(@Param("examId") Long examId);

    List<ExamRecord> selectPageByExamId(@Param("examId") Long examId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT * FROM t_exam_record WHERE status = 0 AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<ExamRecord> selectProcessingRecords();

    @Select("SELECT * FROM t_exam_record WHERE exam_id = #{examId} AND status IN (1, 2) AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<ExamRecord> selectSubmittedByExamId(@Param("examId") Long examId);

    @Select("SELECT COUNT(*) FROM t_exam_record WHERE exam_id = #{examId} AND status IN (1, 2) AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countSubmittedByExamId(@Param("examId") Long examId);

    List<ExamRecord> selectSubmittedPageByExamId(@Param("examId") Long examId, @Param("offset") int offset, @Param("size") int size);

    int updateStatusAndScore(ExamRecord record);

    @Update("UPDATE t_exam_record SET tab_switch_count = tab_switch_count + 1, update_time = NOW() WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void incrementTabSwitch(@Param("id") Long id);

    @Update("UPDATE t_exam_record SET tab_switch_count = tab_switch_count + 1, version = version + 1, update_time = NOW() WHERE id = #{id} AND version = #{version} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int incrementTabSwitchWithVersion(@Param("id") Long id, @Param("version") Integer version);

    @Select("SELECT tab_switch_count FROM t_exam_record WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int selectTabSwitchCount(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM t_exam_record WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countAll();

    /** 批量查询多个考试的已提交记录数（用于消除 N+1 查询） */
    @Select({"<script>",
            "SELECT exam_id, COUNT(*) AS cnt FROM t_exam_record",
            " WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE,
            " AND status IN (1, 2)",
            " AND exam_id IN <foreach collection='examIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
            " GROUP BY exam_id",
            "</script>"})
    List<java.util.Map<String, Object>> countSubmittedByExamIds(@Param("examIds") List<Long> examIds);

    /** 批量查询某用户在指定考试列表中的记录（用于消除 N+1 查询） */
    List<ExamRecord> selectByUserIdAndExamIds(@Param("userId") Long userId, @Param("examIds") List<Long> examIds);

    /** 查询最近 7 天内的考试记录（用于趋势图，在 Java 层按日期分组） */
    @Select("SELECT create_time FROM t_exam_record WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " AND create_time >= #{startDate}")
    List<ExamRecord> selectRecentForTrend(@Param("startDate") java.time.LocalDateTime startDate);

    /** 查询最近提交的考试记录（用于活动动态） */
    List<java.util.Map<String, Object>> selectRecentSubmissions();
}
