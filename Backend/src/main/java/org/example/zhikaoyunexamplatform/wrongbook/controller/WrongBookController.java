package org.example.zhikaoyunexamplatform.wrongbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.wrongbook.service.WrongBookService;
import org.example.zhikaoyunexamplatform.wrongbook.vo.WrongBookVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wrong-book")
@RequiredArgsConstructor
public class WrongBookController {

    private final WrongBookService wrongBookService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Result<PageResult<WrongBookVO>> getMyWrongBooks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer mastered) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(wrongBookService.getMyWrongBooks(userId, page, size, type, mastered));
    }

    @PutMapping("/{id}/master")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> markMastered(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        wrongBookService.markMastered(userId, id);
        return Result.success("标记已掌握");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> deleteWrongBook(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        wrongBookService.deleteWrongBook(userId, id);
        return Result.success("删除成功");
    }
}
