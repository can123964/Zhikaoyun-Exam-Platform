package org.example.zhikaoyunexamplatform.common.controller;

import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.entity.SysLog;
import org.example.zhikaoyunexamplatform.common.mapper.SysLogMapper;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sys-log")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogMapper sysLogMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<SysLog>> listLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        int total = sysLogMapper.countByKeyword(keyword);
        int offset = (page - 1) * size;
        List<SysLog> records = sysLogMapper.selectPage(keyword, offset, size);
        return Result.success(PageResult.of((long) total, page, size, records));
    }
}
