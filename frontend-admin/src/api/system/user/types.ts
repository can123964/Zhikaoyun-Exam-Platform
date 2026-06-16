/**
 * 用户类型定义（适配智考云后端 UserVO / UserCreateDTO / UserUpdateDTO）
 *
 * 后端 UserVO 字段：
 *   id, username, realName, role, roleName, studentNo, classId,
 *   phone, email, avatar, status, lastLoginTime, loginCount, createTime
 *
 * 后端 UserCreateDTO 字段：
 *   username, password, realName, role, studentNo, classId, phone, email, remark
 *
 * 后端 UserUpdateDTO 字段：
 *   realName, classId, phone, email, status, remark
 */

/** 用户分页查询参数 */
export interface UserQueryParams {
  page: number;
  size: number;
  /** 搜索关键字（用户名或真实姓名） */
  keyword?: string;
  /** 角色筛选：0=学生 1=教师 2=管理员 */
  role?: number;
}

/** 用户列表项（对应后端 UserVO） */
export interface UserItem {
  id: number;
  username: string;
  realName: string;
  role: number;
  roleName: string;
  studentNo?: string;
  classId?: number;
  phone?: string;
  email?: string;
  avatar?: string;
  status: number;
  lastLoginTime?: string;
  loginCount?: number;
  createTime?: string;
}

/** 创建用户表单（对应后端 UserCreateDTO） */
export interface UserCreateForm {
  username: string;
  password: string;
  realName: string;
  role: number;
  studentNo?: string;
  classId?: number;
  phone?: string;
  email?: string;
  remark?: string;
}

/** 修改用户表单（对应后端 UserUpdateDTO） */
export interface UserUpdateForm {
  realName?: string;
  classId?: number;
  phone?: string;
  email?: string;
  status?: number;
  remark?: string;
}

/** 统一用户表单（创建/修改合并） */
export interface UserForm {
  id?: number;
  username?: string;
  password?: string;
  realName?: string;
  role?: number;
  studentNo?: string;
  classId?: number;
  phone?: string;
  email?: string;
  status?: number;
  remark?: string;
}

/** 修改密码表单（对应后端 UpdatePasswordDTO） */
export interface PasswordChangeForm {
  oldPassword: string;
  newPassword: string;
}

/** 管理员重置密码表单（对应后端 AdminResetPasswordDTO） */
export interface AdminResetPasswordForm {
  newPassword: string;
}

/** 用户修改个人信息表单（对应后端 ProfileUpdateDTO） */
export interface ProfileUpdateForm {
  realName?: string;
  phone?: string;
  email?: string;
}
