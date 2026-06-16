package org.example.zhikaoyunexamplatform.record.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.record.entity.AnswerDetail;

import java.util.List;

@Mapper
public interface AnswerDetailMapper {

    @Insert("INSERT INTO t_answer_detail (record_id, question_id, user_answer, is_correct, score, is_deleted, create_time, update_time) " +
            "VALUES (#{recordId}, #{questionId}, #{userAnswer}, #{isCorrect}, #{score}, " +
            DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AnswerDetail detail);

    @Select("SELECT * FROM t_answer_detail WHERE record_id = #{recordId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    List<AnswerDetail> selectByRecordId(Long recordId);

    @Select("SELECT * FROM t_answer_detail WHERE record_id = #{recordId} AND question_id = #{questionId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    AnswerDetail selectByRecordIdAndQuestionId(@Param("recordId") Long recordId, @Param("questionId") Long questionId);

    @Update("UPDATE t_answer_detail SET user_answer = #{userAnswer}, update_time = NOW() WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void updateAnswer(AnswerDetail detail);

    @Update("UPDATE t_answer_detail SET is_correct = #{isCorrect}, score = #{score}, update_time = NOW() WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void updateCorrect(AnswerDetail detail);
}
