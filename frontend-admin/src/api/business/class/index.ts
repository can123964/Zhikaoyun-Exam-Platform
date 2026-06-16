import request from "@/utils/request";
import type {
  ClassRoomVO,
  ClassScoreVO,
  ClassCreateForm,
  ClassUpdateForm,
} from "./types";

const ClassAPI = {
  /** 获取班级列表（GET /api/class/list） */
  list(keyword?: string, grade?: string) {
    return request<any, ClassRoomVO[]>({
      url: "/api/class/list",
      method: "get",
      params: {
        keyword: keyword || undefined,
        grade: grade || undefined,
      },
    });
  },

  /** 新增班级（POST /api/class） */
  create(data: ClassCreateForm) {
    return request({
      url: "/api/class",
      method: "post",
      data: {
        className: data.className,
        grade: data.grade,
        teacherId: data.teacherId || undefined,
        remark: data.remark || undefined,
      },
    });
  },

  /** 修改班级（PUT /api/class/{id}） */
  update(id: number, data: ClassUpdateForm) {
    return request({
      url: `/api/class/${id}`,
      method: "put",
      data: {
        className: data.className || undefined,
        grade: data.grade || undefined,
        teacherId: data.teacherId || undefined,
        remark: data.remark || undefined,
      },
    });
  },

  /** 删除班级（DELETE /api/class/{id}） */
  delete(id: number) {
    return request({
      url: `/api/class/${id}`,
      method: "delete",
    });
  },

  /** 获取当前教师的班级列表（GET /api/class/my） */
  getMyClasses() {
    return request<any, ClassRoomVO[]>({
      url: "/api/class/my",
      method: "get",
    });
  },

  /** 获取班级学生列表（GET /api/class/{classId}/students） */
  getStudents(classId: number) {
    return request<any, any[]>({
      url: `/api/class/${classId}/students`,
      method: "get",
    });
  },

  /** 将学生添加到班级（PUT /api/user/{userId}/class） */
  addStudent(classId: number, userId: number) {
    return request({
      url: `/api/user/${userId}/class`,
      method: "put",
      data: { classId },
    });
  },

  /** 将学生从班级移除（classId=null 即取消班级归属） */
  removeStudent(userId: number) {
    return request({
      url: `/api/user/${userId}/class`,
      method: "put",
      data: { classId: null },
    });
  },

  /** 获取班级成绩（GET /api/class/{classId}/scores） */
  getScores(classId: number, examId?: number) {
    return request<any, ClassScoreVO[]>({
      url: `/api/class/${classId}/scores`,
      method: "get",
      params: {
        examId: examId || undefined,
      },
    });
  },
};

export default ClassAPI;
export * from "./types";
