import request from "@/utils/request";
import type { DashboardVO, ActivityPageResult } from "./types";

const DashboardAPI = {
  /** 获取数据看板概览（GET /api/dashboard） */
  getOverview() {
    return request<any, DashboardVO>({
      url: "/api/dashboard",
      method: "get",
    });
  },

  /** 获取最近活动动态（GET /api/dashboard/activity，分页） */
  getActivity(page: number = 1, pageSize: number = 10) {
    return request<any, ActivityPageResult>({
      url: "/api/dashboard/activity",
      method: "get",
      params: { page, pageSize },
    });
  },
};

export default DashboardAPI;
export * from "./types";
