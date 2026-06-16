import request from "@/utils/request";
import type {
  QuestionVO,
  QuestionForm,
  QuestionQueryParams,
  SelectionVO,
  ImportResultVO,
} from "./types";
import type { PageResult } from "@/api/common";

const QuestionAPI = {
  /** 获取题库分页列表（GET /api/question/list） */
  getPage(params: QuestionQueryParams) {
    return request<any, PageResult<QuestionVO>>({
      url: "/api/question/list",
      method: "get",
      params: {
        page: params.page,
        size: params.size,
        type: params.type || undefined,
        difficulty: params.difficulty || undefined,
        category: params.category || undefined,
        keyword: params.keyword || undefined,
        auditStatus: params.auditStatus || undefined,
      },
    });
  },

  /** 获取题目详情（GET /api/question/{id}） */
  getById(id: number) {
    return request<any, QuestionVO>({
      url: `/api/question/${id}`,
      method: "get",
    });
  },

  /** 新增题目（POST /api/question） */
  create(data: QuestionForm) {
    return request({
      url: "/api/question",
      method: "post",
      data: {
        type: data.type,
        content: data.content,
        image: data.image || undefined,
        optionA: data.optionA,
        optionB: data.optionB,
        optionC: data.optionC || undefined,
        optionD: data.optionD || undefined,
        answer: data.answer,
        explanation: data.explanation || undefined,
        difficulty: data.difficulty,
        category: data.category || undefined,
      },
    });
  },

  /** 修改题目（PUT /api/question/{id}） */
  update(id: number, data: QuestionForm) {
    return request({
      url: `/api/question/${id}`,
      method: "put",
      data: {
        type: data.type,
        content: data.content,
        image: data.image || undefined,
        optionA: data.optionA,
        optionB: data.optionB,
        optionC: data.optionC || undefined,
        optionD: data.optionD || undefined,
        answer: data.answer,
        explanation: data.explanation || undefined,
        difficulty: data.difficulty,
        category: data.category || undefined,
      },
    });
  },

  /** 删除题目（DELETE /api/question/{id}） */
  delete(id: number) {
    return request({
      url: `/api/question/${id}`,
      method: "delete",
    });
  },

  /** 获取题目筛选选项（GET /api/question/selections） */
  getSelections() {
    return request<any, SelectionVO>({
      url: "/api/question/selections",
      method: "get",
    });
  },

  /** Excel 导入题目（POST /api/question/import） */
  importExcel(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return request<any, ImportResultVO>({
      url: "/api/question/import",
      method: "post",
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
};

export default QuestionAPI;
export * from "./types";
