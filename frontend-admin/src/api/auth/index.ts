import request from "@/utils/request";
import type { LoginRequest, LoginResponse } from "./types";

const AuthAPI = {
  /** 登录 */
  login(data: LoginRequest) {
    return request<any, LoginResponse>({
      url: "/api/user/login",
      method: "post",
      data,
    });
  },

  /** 退出登录（后端暂无此接口，仅前端清除状态） */
  logout() {
    return Promise.resolve();
  },
};

export default AuthAPI;

// 重导出类型
export * from "./types";
