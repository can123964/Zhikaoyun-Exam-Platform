package org.example.zhikaoyunexamplatform.score.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.score.vo.RankVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreItemVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreStatVO;

import java.util.List;

public interface ScoreService {

    /** 考试成绩列表（分页） */
    PageResult<ScoreItemVO> getExamScores(Long examId, Integer page, Integer size, String keyword, Long classId);

    /** 成绩统计分析 */
    ScoreStatVO getExamStat(Long examId, Long classId);

    /** 成绩排名 */
    List<RankVO> getExamRank(Long examId, Long classId);

    /** 导出成绩 Excel */
    void exportExamScores(Long examId, String keyword, Long classId, HttpServletResponse response);
}
