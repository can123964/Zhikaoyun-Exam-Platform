package org.example.zhikaoyunexamplatform.dashboard.service;

import org.example.zhikaoyunexamplatform.dashboard.vo.DashboardVO;

import java.util.Map;

public interface DashboardService {

    /** 获取首页数据概览（按角色过滤：管理员看全局，教师看自己的） */
    DashboardVO getOverview(Long userId, Integer role);

    /** 获取最近活动动态（分页） */
    Map<String, Object> getRecentActivity(Long userId, Integer role, int page, int pageSize);
}
