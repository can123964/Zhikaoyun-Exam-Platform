package org.example.zhikaoyunexamplatform.record.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.record.dto.SaveAnswerDTO;
import org.example.zhikaoyunexamplatform.record.service.ExamRecordService;
import org.example.zhikaoyunexamplatform.record.vo.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class ExamRecordController {

    private final ExamRecordService examRecordService;

    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<AvailableExamVO>> getAvailableExams() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(examRecordService.getAvailableExams(userId));
    }

    @PostMapping("/{examId}/enter")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<ExamPaperVO> enterExam(@PathVariable Long examId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(examRecordService.enterExam(userId, examId));
    }

    @PostMapping("/{recordId}/answer")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> saveAnswer(@PathVariable Long recordId, @Valid @RequestBody SaveAnswerDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        examRecordService.saveAnswer(userId, recordId, dto);
        return Result.success("保存成功");
    }

    @PostMapping("/{recordId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Integer> submitExam(@PathVariable Long recordId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer totalScore = examRecordService.submitExam(userId, recordId);
        return Result.success("交卷成功", totalScore);
    }

    @PostMapping("/{recordId}/tab-switch")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Map<String, Object>> tabSwitch(@PathVariable Long recordId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Object> result = examRecordService.tabSwitch(userId, recordId);
        return Result.success(result);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<PageResult<ExamRecordVO>> getMyRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(examRecordService.getMyRecords(userId, page, size));
    }

    @GetMapping("/detail/{recordId}")
    @PreAuthorize("isAuthenticated()")
    public Result<ExamRecordVO> getRecordDetail(@PathVariable Long recordId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(examRecordService.getRecordDetail(userId, recordId));
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<PageResult<ExamRecordVO>> getExamRecords(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(examRecordService.getExamRecords(examId, page, size));
    }

    @GetMapping("/score/{examId}/stat")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<ExamScoreStatVO> getExamScoreStat(@PathVariable Long examId) {
        return Result.success(examRecordService.getExamScoreStat(examId));
    }

    @GetMapping("/score/{examId}/rank")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<PageResult<StudentScoreVO>> getExamScoreRank(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(examRecordService.getExamScoreRank(examId, page, size));
    }
}
