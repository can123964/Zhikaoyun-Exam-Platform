import request from "@/utils/request";
import type {
  ExamVO,
  ExamCreateForm,
  ExamUpdateForm,
  ExamQueryParams,
  ExamQuestionVO,
  RandomPaperForm,
} from "./types";
import type { PageResult } from "@/api/common";

const ExamAPI = {
  /** 获取考试分页列表（GET /api/exam） */
  getPage(params: ExamQueryParams) {
    return request<any, PageResult<ExamVO>>({
      url: "/api/exam",
      method: "get",
      params: {
        page: params.page,
        size: params.size,
        status: params.status || undefined,
        keyword: params.keyword || undefined,
      },
    });
  },

  /** 获取考试详情（GET /api/exam/{id}） */
  getById(id: number) {
    return request<any, ExamVO>({
      url: `/api/exam/${id}`,
      method: "get",
    });
  },

  /** 创建考试（POST /api/exam） */
  create(data: ExamCreateForm) {
    return request({
      url: "/api/exam",
      method: "post",
      data,
    });
  },

  /** 修改考试（PUT /api/exam/{id}） */
  update(id: number, data: ExamUpdateForm) {
    return request({
      url: `/api/exam/${id}`,
      method: "put",
      data,
    });
  },

  /** 删除考试（DELETE /api/exam/{id}） */
  delete(id: number) {
    return request({
      url: `/api/exam/${id}`,
      method: "delete",
    });
  },

  /** 发布考试（PUT /api/exam/{id}/publish） */
  publish(id: number) {
    return request({
      url: `/api/exam/${id}/publish`,
      method: "put",
    });
  },

  /** 结束考试（PUT /api/exam/{id}/end） */
  end(id: number) {
    return request({
      url: `/api/exam/${id}/end`,
      method: "put",
    });
  },

  /** 获取考试题目列表（GET /api/exam/{examId}/questions） */
  getQuestions(examId: number) {
    return request<any, ExamQuestionVO[]>({
      url: `/api/exam/${examId}/questions`,
      method: "get",
    });
  },

  /** 添加考试题目（POST /api/exam/{examId}/question） */
  addQuestion(examId: number, questionId: number, score: number) {
    return request({
      url: `/api/exam/${examId}/question`,
      method: "post",
      params: {
        questionId,
        score,
      },
    });
  },

  /** 移除考试题目（DELETE /api/exam/{examId}/question/{questionId}） */
  removeQuestion(examId: number, questionId: number) {
    return request({
      url: `/api/exam/${examId}/question/${questionId}`,
      method: "delete",
    });
  },

  /** 随机组卷（POST /api/exam/{examId}/random-questions） */
  randomPaper(examId: number, data: RandomPaperForm) {
    return request<any, ExamQuestionVO[]>({
      url: `/api/exam/${examId}/random-questions`,
      method: "post",
      data,
    });
  },
};

export default ExamAPI;
export * from "./types";
