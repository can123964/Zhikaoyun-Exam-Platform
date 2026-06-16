package org.example.zhikaoyunexamplatform.score.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.score.service.ScoreService;
import org.example.zhikaoyunexamplatform.score.vo.RankVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreItemVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreStatVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    /** 考试成绩列表 */
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<PageResult<ScoreItemVO>> getExamScores(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long classId) {
        return Result.success(scoreService.getExamScores(examId, page, size, keyword, classId));
    }

    /** 成绩统计分析 */
    @GetMapping("/exam/{examId}/stat")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<ScoreStatVO> getExamStat(@PathVariable Long examId,
                                           @RequestParam(required = false) Long classId) {
        return Result.success(scoreService.getExamStat(examId, classId));
    }

    /** 成绩排名 */
    @GetMapping("/exam/{examId}/rank")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<List<RankVO>> getExamRank(@PathVariable Long examId,
                                            @RequestParam(required = false) Long classId) {
        return Result.success(scoreService.getExamRank(examId, classId));
    }

    /** 导出成绩 Excel */
    @GetMapping("/exam/{examId}/export")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public void exportExamScores(
            @PathVariable Long examId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long classId,
            HttpServletResponse response) {
        scoreService.exportExamScores(examId, keyword, classId, response);
    }
}
