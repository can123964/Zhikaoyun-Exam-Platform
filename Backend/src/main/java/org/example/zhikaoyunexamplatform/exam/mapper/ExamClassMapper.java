package org.example.zhikaoyunexamplatform.exam.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;

import java.util.List;

@Mapper
public interface ExamClassMapper {

    @Select("SELECT class_id FROM t_exam_class WHERE exam_id = #{examId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<Long> selectClassIdsByExamId(Long examId);

    @Select("SELECT DISTINCT exam_id FROM t_exam_class WHERE class_id = #{classId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<Long> selectExamIdsByClassId(Long classId);

    @Select("SELECT COUNT(*) FROM t_exam_class WHERE exam_id = #{examId} AND class_id = #{classId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByExamIdAndClassId(@Param("examId") Long examId, @Param("classId") Long classId);

    @Insert("INSERT INTO t_exam_class (exam_id, class_id, is_deleted, create_time) " +
            "VALUES (#{examId}, #{classId}, " + DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW())")
    void insert(@Param("examId") Long examId, @Param("classId") Long classId);

    @Update("UPDATE t_exam_class SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE exam_id = #{examId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void deleteByExamId(Long examId);

    @Select("SELECT COUNT(*) FROM t_exam_class WHERE exam_id = #{examId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByExamId(Long examId);

    /** 批量查询多个考试的班级关联（返回 exam_id + class_id 对，用于消除 N+1） */
    @Select({"<script>",
            "SELECT exam_id, class_id FROM t_exam_class WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE,
            " AND exam_id IN <foreach collection='examIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
            "</script>"})
    List<java.util.Map<String, Long>> selectByExamIds(@Param("examIds") List<Long> examIds);
}
