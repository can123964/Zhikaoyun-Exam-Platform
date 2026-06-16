/** 题目列表项（对应后端 QuestionVO） */
export interface QuestionVO {
  id: number;
  type: number;
  content: string;
  image?: string;
  optionA: string;
  optionB: string;
  optionC?: string;
  optionD?: string;
  answer: string;
  explanation?: string;
  difficulty: number;
  category?: string;
  auditStatus?: number;
  createTime?: string;
}

/** 创建/修改题目表单（对应后端 QuestionDTO） */
export interface QuestionForm {
  type: number;
  content: string;
  image?: string;
  optionA: string;
  optionB: string;
  optionC?: string;
  optionD?: string;
  answer: string;
  explanation?: string;
  difficulty: number;
  category?: string;
}

/** 题目分页查询参数 */
export interface QuestionQueryParams {
  page: number;
  size: number;
  type?: number;
  difficulty?: number;
  category?: string;
  keyword?: string;
  auditStatus?: number;
}

/** 题目筛选选项（对应后端 SelectionVO） */
export interface SelectionVO {
  categories: string[];
}

/** Excel 导入结果（对应后端 ImportResultVO） */
export interface ImportResultVO {
  successCount: number;
  failCount: number;
  message?: string;
}
