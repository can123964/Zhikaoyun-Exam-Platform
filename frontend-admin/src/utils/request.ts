import axios, { type InternalAxiosRequestConfig, type AxiosResponse } from "axios";
import qs from "qs";
import type { ApiResponse } from "@/api/common";
import { ApiCodeEnum } from "@/enums/api";
import { getToken, redirectToLogin } from "@/utils/auth";

// HTTP 请求实例
const http = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
  paramsSerializer: (params) => qs.stringify(params, { arrayFormat: "repeat" }),
});

// 请求拦截器：自动附加 JWT Token
http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器
http.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { responseType } = response.config;

    // 二进制数据（blob / arraybuffer）直接返回原始 response
    if (responseType === "blob" || responseType === "arraybuffer") {
      return response;
    }

    const { code, data, msg } = response.data;

    if (code === ApiCodeEnum.SUCCESS) {
      // 成功：直接返回 data 字段，业务代码直接使用 res.xxx / res.records
      return data as any;
    }

    // 401 未登录 → 跳转登录页
    if (code === ApiCodeEnum.UNAUTHORIZED) {
      redirectToLogin(msg || "登录已过期，请重新登录");
      return Promise.reject(new Error(msg || "未登录"));
    }

    // 403 无权限
    if (code === ApiCodeEnum.FORBIDDEN) {
      ElMessage.error(msg || "权限不足");
      return Promise.reject(new Error(msg || "权限不足"));
    }

    // 其他业务错误
    ElMessage.error(msg || "请求失败");
    return Promise.reject(new Error(msg || "请求失败"));
  },

  (error) => {
    // 网络错误或 HTTP 状态码异常
    if (error.response) {
      const status = error.response.status;
      if (status === 401) {
        redirectToLogin("登录已过期，请重新登录");
      } else if (status === 403) {
        ElMessage.error("权限不足");
      } else {
        ElMessage.error(error.response.data?.msg || "请求失败");
      }
    } else {
      ElMessage.error("网络连接失败");
    }
    return Promise.reject(error);
  }
);

export default http;
