package org.example.zhikaoyunexamplatform.question.mapper;

import org.apache.ibatis.annotations.*;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.question.entity.Question;
import org.example.zhikaoyunexamplatform.question.vo.QuestionVO;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Select("SELECT * FROM t_question WHERE id = #{id} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    Question selectById(Long id);

    @Select("SELECT DISTINCT category FROM t_question WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE + " AND category IS NOT NULL AND category != '' ORDER BY category")
    List<String> selectDistinctCategories();

    @Insert("INSERT INTO t_question (type, content, image, option_a, option_b, option_c, option_d, " +
            "answer, explanation, difficulty, category, audit_status, " +
            "creator_id, remark, is_deleted, create_time, update_time) " +
            "VALUES (#{type}, #{content}, #{image}, #{optionA}, #{optionB}, #{optionC}, #{optionD}, " +
            "#{answer}, #{explanation}, #{difficulty}, #{category}, #{auditStatus}, " +
            "#{creatorId}, #{remark}, " + DeletedStatusEnum.NOT_DELETED_VALUE + ", NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Question question);

    @Update("UPDATE t_question SET is_deleted = " + DeletedStatusEnum.DELETED_VALUE + ", update_time = NOW() WHERE id = #{id}")
    void deleteById(Long id);

    void updateById(Question question);

    int countByConditions(@Param("type") Integer type,
                          @Param("difficulty") Integer difficulty,
                          @Param("category") String category,
                          @Param("keyword") String keyword,
                          @Param("auditStatus") Integer auditStatus,
                          @Param("creatorId") Long creatorId);

    List<QuestionVO> selectPage(@Param("type") Integer type,
                                @Param("difficulty") Integer difficulty,
                                @Param("category") String category,
                                @Param("keyword") String keyword,
                                @Param("auditStatus") Integer auditStatus,
                                @Param("offset") int offset,
                                @Param("size") int size,
                                @Param("creatorId") Long creatorId);

    List<Question> selectRandomCandidates(@Param("types") List<Integer> types,
                                          @Param("difficulties") List<Integer> difficulties,
                                          @Param("categories") List<String> categories,
                                          @Param("count") Integer count);

    /** 按单条规则随机抽题（type + 可选 difficulty + 可选 category） */
    List<Question> selectRandomByRule(@Param("type") Integer type,
                                      @Param("difficulty") Integer difficulty,
                                      @Param("category") String category,
                                      @Param("count") Integer count);

    @Select("SELECT COUNT(*) FROM t_question WHERE is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countAll();

    @Select("SELECT COUNT(*) FROM t_question WHERE creator_id = #{creatorId} AND is_deleted = " + DeletedStatusEnum.NOT_DELETED_VALUE)
    int countByCreatorId(Long creatorId);

    /** 批量根据 ID 列表查询题目（用于消除 N+1 查询） */
    List<Question> selectByIds(@Param("ids") List<Long> ids);
}
