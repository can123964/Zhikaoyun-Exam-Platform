package org.example.zhikaoyunexamplatform.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.result.Result;
import org.example.zhikaoyunexamplatform.common.utils.SecurityUtils;
import org.example.zhikaoyunexamplatform.dashboard.service.DashboardService;
import org.example.zhikaoyunexamplatform.dashboard.vo.DashboardVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<DashboardVO> getDashboard() {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer role = SecurityUtils.getCurrentUserRole();
        return Result.success(dashboardService.getOverview(userId, role));
    }

    @GetMapping("/activity")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> getActivity(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer role = SecurityUtils.getCurrentUserRole();
        return Result.success(dashboardService.getRecentActivity(userId, role, page, pageSize));
    }
}
