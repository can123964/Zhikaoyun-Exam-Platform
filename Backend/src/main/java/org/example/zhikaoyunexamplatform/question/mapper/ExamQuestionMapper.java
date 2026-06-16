package org.example.zhikaoyunexamplatform.question.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.exam.entity.ExamQuestion;

import java.util.List;

@Mapper
public interface ExamQuestionMapper {

    @Select("SELECT COUNT(*) FROM t_exam_question WHERE question_id = #{questionId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByQuestionId(Long questionId);

    @Select("SELECT * FROM t_exam_question WHERE exam_id = #{examId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " ORDER BY sort_order ASC")
    List<ExamQuestion> selectByExamId(Long examId);

    @Insert("INSERT INTO t_exam_question (exam_id, question_id, score, sort_order, is_deleted, create_time, update_time) " +
            "VALUES (#{examId}, #{questionId}, #{score}, #{sortOrder}, " + DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    void insert(ExamQuestion eq);

    @Update("UPDATE t_exam_question SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE exam_id = #{examId} AND question_id = #{questionId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void deleteByExamIdAndQuestionId(@Param("examId") Long examId, @Param("questionId") Long questionId);

    @Update("UPDATE t_exam_question SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE exam_id = #{examId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void deleteByExamId(Long examId);

    @Select("SELECT COUNT(*) FROM t_exam_question WHERE exam_id = #{examId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByExamId(Long examId);

    @Select("SELECT COUNT(*) FROM t_exam_question WHERE exam_id = #{examId} AND question_id = #{questionId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByExamIdAndQuestionId(@Param("examId") Long examId, @Param("questionId") Long questionId);

    /** 批量查询多个考试的题目数量（用于消除 N+1 查询） */
    List<ExamQuestion> selectByExamIds(@Param("examIds") List<Long> examIds);

    /** 批量插入多条考试题目关联记录 */
    void batchInsert(@Param("list") List<ExamQuestion> list);
}
