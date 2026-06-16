import request from "@/utils/request";
import type {
  UserItem,
  UserForm,
  UserQueryParams,
} from "./types";

const UserAPI = {
  /** 获取当前登录用户信息（GET /api/user/info → UserVO） */
  getInfo() {
    return request<any, UserItem>({
      url: "/api/user/info",
      method: "get",
    });
  },

  /** 获取用户分页列表（GET /api/user/list） */
  getPage(queryParams: UserQueryParams) {
    return request<any, { records: UserItem[]; total: number; pages: number; current: number; size: number }>({
      url: "/api/user/list",
      method: "get",
      params: {
        page: queryParams.page,
        size: queryParams.size,
        role: queryParams.role,
        keyword: queryParams.keyword || undefined,
      },
    });
  },

  /** 新增用户（POST /api/user） */
  create(data: UserForm) {
    return request({
      url: "/api/user",
      method: "post",
      data: {
        username: data.username,
        password: data.password,
        realName: data.realName,
        role: data.role,
        studentNo: data.studentNo || undefined,
        classId: data.classId || undefined,
        phone: data.phone || undefined,
        email: data.email || undefined,
        remark: data.remark || undefined,
      },
    });
  },

  /** 修改用户（PUT /api/user/{id}） */
  update(id: number, data: UserForm) {
    return request({
      url: `/api/user/${id}`,
      method: "put",
      data: {
        realName: data.realName,
        classId: data.classId,
        phone: data.phone,
        email: data.email,
        status: data.status,
        remark: data.remark,
      },
    });
  },

  /** 删除用户（DELETE /api/user/{id}） */
  deleteById(id: number) {
    return request({
      url: `/api/user/${id}`,
      method: "delete",
    });
  },

  /**
   * 修改密码（PUT /api/user/password）
   * 对应后端 UpdatePasswordDTO：{ oldPassword, newPassword }
   */
  changePassword(data: { oldPassword: string; newPassword: string }) {
    return request({
      url: "/api/user/password",
      method: "put",
      data,
    });
  },

  /** 管理员重置用户密码（PUT /api/user/{id}/reset-password） */
  resetPassword(id: number, newPassword: string) {
    return request({
      url: `/api/user/${id}/reset-password`,
      method: "put",
      data: { newPassword },
    });
  },

  /** 用户修改个人信息（PUT /api/user/profile） */
  updateProfile(data: { realName?: string; phone?: string; email?: string }) {
    return request({
      url: "/api/user/profile",
      method: "put",
      data,
    });
  },

  /** 导出用户（后端暂无此接口，预留） */
  export() {
    return Promise.reject(new Error("暂不支持导出"));
  },

  /** 导入用户（后端暂无此接口，预留） */
  import() {
    return Promise.reject(new Error("暂不支持导入"));
  },
};

export default UserAPI;
export * from "./types";
