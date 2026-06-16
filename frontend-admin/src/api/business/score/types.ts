/** 考试成绩列表项（对应后端 ScoreItemVO） */
export interface ScoreItemVO {
  userId: number;
  username?: string;
  realName?: string;
  studentNo?: string;
  className?: string;
  score: number;
  totalScore: number;
  passScore?: number;
  status: number;
  statusName: string;
  isPassed?: boolean;
  submitTime?: string;
  duration?: number;
}

/** 成绩统计分析（对应后端 ScoreStatVO） */
export interface ScoreStatVO {
  examId: number;
  examTitle: string;
  avgScore: number;
  maxScore: number;
  minScore: number;
  passScore?: number;
  passRate: number;
  submitCount: number;
  scoreDistribution?: Record<string, number>;
}

/** 成绩排名项（对应后端 RankVO） */
export interface RankVO {
  rank: number;
  userId: number;
  realName?: string;
  studentNo?: string;
  score: number;
  status: number;
  submitTime?: string;
}
