package org.example.zhikaoyunexamplatform.exam.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.exam.dto.ExamCreateDTO;
import org.example.zhikaoyunexamplatform.exam.dto.ExamUpdateDTO;
import org.example.zhikaoyunexamplatform.exam.dto.RandomPaperDTO;
import org.example.zhikaoyunexamplatform.exam.service.ExamService;
import org.example.zhikaoyunexamplatform.exam.vo.ExamQuestionVO;
import org.example.zhikaoyunexamplatform.exam.vo.ExamVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<PageResult<ExamVO>> listExams(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(examService.listExams(page, size, status, keyword));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<ExamVO> getExamById(@PathVariable Long id) {
        return Result.success(examService.getExamById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<ExamVO> createExam(@Valid @RequestBody ExamCreateDTO dto) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        return Result.success("创建成功", examService.createExam(dto, creatorId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<ExamVO> updateExam(@PathVariable Long id, @Valid @RequestBody ExamUpdateDTO dto) {
        return Result.success("修改成功", examService.updateExam(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> publishExam(@PathVariable Long id) {
        examService.publishExam(id);
        return Result.success("发布成功");
    }

    @PutMapping("/{id}/end")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> endExam(@PathVariable Long id) {
        examService.endExam(id);
        return Result.success("结束成功");
    }

    @GetMapping("/{examId}/questions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<List<ExamQuestionVO>> getExamQuestions(@PathVariable Long examId) {
        return Result.success(examService.getExamQuestions(examId));
    }

    @PostMapping("/{examId}/question")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> addQuestionToExam(@PathVariable Long examId,
                                             @RequestParam Long questionId,
                                             @RequestParam(defaultValue = "5") Integer score) {
        examService.addQuestionToExam(examId, questionId, score);
        return Result.success("添加成功");
    }

    @DeleteMapping("/{examId}/question/{questionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> removeQuestionFromExam(@PathVariable Long examId, @PathVariable Long questionId) {
        examService.removeQuestionFromExam(examId, questionId);
        return Result.success("移除成功");
    }

    @PostMapping("/{examId}/random-questions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<List<ExamQuestionVO>> randomPaper(@PathVariable Long examId, @RequestBody @Valid RandomPaperDTO dto) {
        return Result.success("组卷成功", examService.randomPaper(examId, dto));
    }
}
