import request from "@/utils/request";
import type { ScoreItemVO, ScoreStatVO, RankVO } from "./types";
import type { PageResult } from "@/api/common";

const ScoreAPI = {
  /** 考试成绩列表（GET /api/score/exam/{examId}） */
  getExamScores(examId: number, params: { page: number; size: number; keyword?: string }) {
    return request<any, PageResult<ScoreItemVO>>({
      url: `/api/score/exam/${examId}`,
      method: "get",
      params: {
        page: params.page,
        size: params.size,
        keyword: params.keyword || undefined,
      },
    });
  },

  /** 成绩统计分析（GET /api/score/exam/{examId}/stat） */
  getExamStat(examId: number, classId?: number) {
    return request<any, ScoreStatVO>({
      url: `/api/score/exam/${examId}/stat`,
      method: "get",
      params: { classId: classId || undefined },
    });
  },

  /** 成绩排名（GET /api/score/exam/{examId}/rank） */
  getExamRank(examId: number, classId?: number) {
    return request<any, RankVO[]>({
      url: `/api/score/exam/${examId}/rank`,
      method: "get",
      params: { classId: classId || undefined },
    });
  },

  /** 导出成绩 Excel（GET /api/score/exam/{examId}/export） */
  exportExamScores(examId: number, keyword?: string) {
    return request({
      url: `/api/score/exam/${examId}/export`,
      method: "get",
      params: { keyword: keyword || undefined },
      responseType: "blob",
    });
  },
};

export default ScoreAPI;
export * from "./types";
