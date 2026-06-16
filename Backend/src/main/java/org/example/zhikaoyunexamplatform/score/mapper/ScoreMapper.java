package org.example.zhikaoyunexamplatform.score.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.zhikaoyunexamplatform.common.enums.DeletedStatusEnum;
import org.example.zhikaoyunexamplatform.score.vo.RankVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreItemVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreStatVO;

import java.util.List;

@Mapper
public interface ScoreMapper {

    /** 考试成绩列表（分页） */
    List<ScoreItemVO> selectScorePage(@Param("examId") Long examId,
                                      @Param("keyword") String keyword,
                                      @Param("classId") Long classId,
                                      @Param("offset") int offset,
                                      @Param("size") int size);

    /** 考试成绩总数 */
    int countScore(@Param("examId") Long examId,
                   @Param("keyword") String keyword,
                   @Param("classId") Long classId);

    /** 成绩统计（平均分/最高/最低/及格率/分段） */
    ScoreStatVO selectExamStat(@Param("examId") Long examId,
                               @Param("classId") Long classId);

    /** 分段统计辅助（移到 XML 中实现，支持动态 classId 过滤） */
    int countByScoreRange(@Param("examId") Long examId,
                          @Param("min") Integer min,
                          @Param("max") Integer max,
                          @Param("classId") Long classId);

    /** 成绩排名 */
    List<RankVO> selectRankList(@Param("examId") Long examId,
                                @Param("classId") Long classId);

    /** 全部考试成绩（不分页，用于导出） */
    List<ScoreItemVO> selectAllScores(@Param("examId") Long examId,
                                      @Param("keyword") String keyword,
                                      @Param("classId") Long classId);
}
