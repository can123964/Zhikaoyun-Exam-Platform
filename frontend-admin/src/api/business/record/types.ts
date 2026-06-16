/** 考试记录项（对应后端 ExamRecordVO） */
export interface ExamRecordVO {
  id: number;
  examId: number;
  examTitle?: string;
  duration?: number;
  score?: number;
  passScore?: number;
  status: number;
  statusName: string;
  totalScore?: number;
  startTime?: string;
  submitTime?: string;
  userId?: number;
  username?: string;
  realName?: string;
  studentNo?: string;
  answerDetails?: AnswerDetailVO[];
}

/** 答题详情项（对应后端 AnswerDetailVO） */
export interface AnswerDetailVO {
  questionId: number;
  type?: number;
  content?: string;
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  studentAnswer?: string;
  correctAnswer?: string;
  isCorrect?: number;
  score?: number;
  explanation?: string;
}

/** 考试成绩统计（对应后端 ExamScoreStatVO，ExamRecordController 使用） */
export interface ExamScoreStatVO {
  examId: number;
  examTitle: string;
  totalStudents: number;
  submittedCount: number;
  avgScore: number;
  maxScore: number;
  minScore: number;
  passRate: number;
}

/** 学生成绩项（对应后端 StudentScoreVO，ExamRecordController 使用） */
export interface StudentScoreVO {
  recordId: number;
  userId?: number;
  username?: string;
  realName?: string;
  studentNo?: string;
  className?: string;
  score: number;
  status: number;
  statusName: string;
  submitTime?: string;
}
