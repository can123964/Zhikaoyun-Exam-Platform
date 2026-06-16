/** 考试列表项（对应后端 ExamVO） */
export interface ExamVO {
  id: number;
  title: string;
  description?: string;
  examType?: number;
  creatorId?: number;
  creatorName?: string;
  duration: number;
  startTime: string;
  endTime: string;
  status: number;
  statusName?: string;
  totalScore?: number;
  passScore?: number;
  showAnswer?: number;
  allowRetry?: number;
  questionOrder?: number;
  optionOrder?: number;
  maxCount?: number;
  maxTabSwitches?: number;
  remark?: string;
  classIds?: number[];
  classNames?: string[];
  questionCount?: number;
  studentCount?: number;
  submittedCount?: number;
  createTime?: string;
}

/** 创建考试表单（对应后端 ExamCreateDTO） */
export interface ExamCreateForm {
  title: string;
  description?: string;
  examType: number;
  duration: number;
  startTime: string;
  endTime: string;
  totalScore?: number;
  passScore?: number;
  showAnswer?: number;
  allowRetry?: number;
  questionOrder?: number;
  optionOrder?: number;
  maxCount?: number;
  maxTabSwitches?: number;
  remark?: string;
  classIds: number[];
}

/** 修改考试表单（对应后端 ExamUpdateDTO） */
export interface ExamUpdateForm extends Partial<ExamCreateForm> {}

/** 考试分页查询参数 */
export interface ExamQueryParams {
  page: number;
  size: number;
  status?: number;
  keyword?: string;
}

/** 考试题目项（对应后端 ExamQuestionVO） */
export interface ExamQuestionVO {
  id?: number;
  examId: number;
  questionId: number;
  score: number;
  sortOrder?: number;
  content?: string;
  type?: number;
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  answer?: string;
}

/** 随机组卷表单（对应后端 RandomQuestionDTO + RandomRule） */
export interface RandomPaperForm {
  rules: RandomRule[];
}

/** 抽题规则（对应后端 RandomQuestionDTO.RandomRule） */
export interface RandomRule {
  type: number;         // 题型：0单选 1多选 2判断
  difficulty?: number;  // 难度：1简单 2中等 3困难
  category?: string;    // 分类
  count: number;        // 抽取数量
  score: number;        // 每题分值
}
