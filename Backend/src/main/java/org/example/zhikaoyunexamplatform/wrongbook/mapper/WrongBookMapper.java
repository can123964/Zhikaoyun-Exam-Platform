package org.example.zhikaoyunexamplatform.wrongbook.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.wrongbook.entity.WrongBook;

import java.util.List;

@Mapper
public interface WrongBookMapper {

    @Select("SELECT * FROM t_wrong_book WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    WrongBook selectById(Long id);

    @Select("SELECT * FROM t_wrong_book WHERE user_id = #{userId} AND question_id = #{questionId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    WrongBook selectByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);

    @Insert("INSERT INTO t_wrong_book (user_id, question_id, record_id, my_answer, wrong_count, mastered, is_deleted, create_time, update_time) " +
            "VALUES (#{userId}, #{questionId}, #{recordId}, #{myAnswer}, #{wrongCount}, #{mastered}, " +
            DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(WrongBook wrongBook);

    @Update("UPDATE t_wrong_book SET wrong_count = wrong_count + 1, update_time = NOW() WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void incrementWrongCount(Long id);

    @Update("UPDATE t_wrong_book SET mastered = 1, update_time = NOW() WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    void updateMastered(@Param("id") Long id, @Param("mastered") Integer mastered);

    @Update("UPDATE t_wrong_book SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE id = #{id}")
    void deleteById(Long id);

    int countByUserId(@Param("userId") Long userId, @Param("type") Integer type, @Param("mastered") Integer mastered);

    List<WrongBook> selectPageByUserId(@Param("userId") Long userId, @Param("type") Integer type,
                                       @Param("mastered") Integer mastered, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_wrong_book WHERE user_id = #{userId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByUserIdSimple(Long userId);

    @Select("SELECT COUNT(*) FROM t_wrong_book WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countAll();

    /** 批量查询某用户在指定题目列表中的错题记录（用于消除 N+1 查询） */
    List<WrongBook> selectByUserIdAndQuestionIds(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);
}
