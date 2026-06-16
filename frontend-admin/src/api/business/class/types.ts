/** 班级列表项（对应后端 ClassRoomVO） */
export interface ClassRoomVO {
  id: number;
  className: string;
  grade: string;
  teacherId?: number;
  teacherName?: string;
  studentCount?: number;
  remark?: string;
  createTime?: string;
}

/** 班级成绩项（对应后端 ClassScoreVO） */
export interface ClassScoreVO {
  recordId?: number;
  userId: number;
  username: string;
  realName: string;
  studentNo?: string;
  examId: number;
  examTitle: string;
  score?: number;
  totalScore?: number;
  status: number;
  statusName: string;
  submitTime?: string;
}

/** 创建班级表单（对应后端 ClassCreateDTO） */
export interface ClassCreateForm {
  className: string;
  grade: string;
  teacherId?: number;
  remark?: string;
}

/** 修改班级表单（对应后端 ClassUpdateDTO） */
export interface ClassUpdateForm {
  className?: string;
  grade?: string;
  teacherId?: number;
  remark?: string;
}
