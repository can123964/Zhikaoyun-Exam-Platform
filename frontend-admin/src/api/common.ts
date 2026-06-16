/**
 * 通用 API 类型定义
 */

/** API 响应结构 */
export interface ApiResponse<T = any> {
  /** 响应码（后端返回 number 类型） */
  code: number;
  /** 响应数据 */
  data: T;
  /** 响应消息 */
  msg: string;
}

/** 基础查询参数（对齐后端分页 @RequestParam page/size） */
export interface BaseQueryParams {
  /** 页码 */
  page: number;
  /** 每页记录数 */
  size: number;
}

/** 分页数据结构（对齐后端 PageResult） */
export interface PageResult<T> {
  /** 数据列表 */
  records: T[];
  /** 总记录数 */
  total: number;
  /** 总页数 */
  pages: number;
  /** 当前页码 */
  current: number;
  /** 每页条数 */
  size: number;
}

/** 下拉选项 */
export interface OptionItem {
  /** 选项值 */
  value: string | number;
  /** 选项标签 */
  label: string;
  /** 子选项 */
  children?: OptionItem[];
}

/** Excel 导入结果 */
export interface ExcelResult {
  /** 响应码 */
  code: string;
  /** 无效数据数量 */
  invalidCount: number;
  /** 有效数据数量 */
  validCount: number;
  /** 错误信息列表 */
  messageList: string[];
}
