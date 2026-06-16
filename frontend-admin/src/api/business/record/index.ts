import request from "@/utils/request";
import type { ExamRecordVO } from "./types";
import type { PageResult } from "@/api/common";

const RecordAPI = {
  /** 获取某场考试的记录（GET /api/record/exam/{examId}）— 教师/管理员 */
  getExamRecords(examId: number, params: { page: number; size: number }) {
    return request<any, PageResult<ExamRecordVO>>({
      url: `/api/record/exam/${examId}`,
      method: "get",
      params: { page: params.page, size: params.size },
    });
  },

  /** 获取考试记录详情（GET /api/record/detail/{recordId}） */
  getRecordDetail(recordId: number) {
    return request<any, ExamRecordVO>({
      url: `/api/record/detail/${recordId}`,
      method: "get",
    });
  },
};

export default RecordAPI;
export * from "./types";
