package org.example.zhikaoyunexamplatform.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.question.dto.QuestionDTO;
import org.example.zhikaoyunexamplatform.question.service.QuestionService;
import org.example.zhikaoyunexamplatform.question.vo.ImportResultVO;
import org.example.zhikaoyunexamplatform.question.vo.QuestionVO;
import org.example.zhikaoyunexamplatform.question.vo.SelectionVO;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<PageResult<QuestionVO>> listQuestions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        return Result.success(questionService.listQuestions(page, size, type, difficulty, category, keyword, auditStatus, currentUserId, currentRole));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<QuestionVO> getQuestionById(@PathVariable Long id) {
        return Result.success(questionService.getQuestionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> createQuestion(@Valid @RequestBody QuestionDTO dto) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        questionService.createQuestion(dto, creatorId);
        return Result.success("题目创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        questionService.updateQuestion(id, dto, currentUserId, currentRole);
        return Result.success("题目修改成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<String> deleteQuestion(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        questionService.deleteQuestion(id, currentUserId, currentRole);
        return Result.success("题目删除成功");
    }

    @GetMapping("/selections")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<SelectionVO> getSelections() {
        return Result.success(questionService.getSelections());
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        return Result.success(questionService.importExcel(file, creatorId));
    }
}
