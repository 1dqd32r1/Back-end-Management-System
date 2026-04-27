import request from '@/utils/request';

export interface SysUser {
  userId?: number;
  userName?: string;
  nickName?: string;
  password?: string;
  email?: string;
  phonenumber?: string;
  sex?: string;
  avatar?: string;
  status?: string;
  createTime?: string;
  updateTime?: string;
}

export interface UserQueryParams {
  pageNum?: number;
  pageSize?: number;
  userName?: string;
  phonenumber?: string;
}

// 查询用户列表
export function listUser(query?: UserQueryParams) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  });
}

// 查询用户详细
export function getUser(userId: number) {
  return request({
    url: '/system/user/' + userId,
    method: 'get'
  });
}

// 新增用户
export function addUser(data: SysUser) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  });
}

// 修改用户
export function updateUser(data: SysUser) {
  return request({
    url: '/system/user',
    method: 'put',
    data: data
  });
}

// 删除用户
export function delUser(userIds: number | string) {
  return request({
    url: '/system/user/' + userIds,
    method: 'delete'
  });
}

// 上传用户头像
export function uploadAvatar(formData: FormData) {
  return request({
    url: '/common/uploadAvatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

// 修改密码
export function updatePassword(data: { oldPassword: string; newPassword: string }) {
  return request({
    url: '/system/user/password',
    method: 'put',
    data: data
  });
}
