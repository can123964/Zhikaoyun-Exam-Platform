/** 趋势数据点 */
export interface TrendItem {
  date: string;
  count: number;
}

/** 活动动态项 */
export interface ActivityItem {
  type: "exam_submit" | "user_register";
  title: string;
  subtitle?: string;
  time?: string;
}

/** 活动动态分页响应 */
export interface ActivityPageResult {
  list: ActivityItem[];
  total: number;
  page: number;
  pageSize: number;
}

/** 数据看板概览（对应后端 DashboardVO） */
export interface DashboardVO {
  totalUsers?: number;
  totalStudents?: number;
  totalTeachers?: number;
  totalQuestions?: number;
  totalExams?: number;
  ongoingExams?: number;
  totalRecords?: number;
  totalWrongBooks?: number;
  examTrend?: TrendItem[];
}
