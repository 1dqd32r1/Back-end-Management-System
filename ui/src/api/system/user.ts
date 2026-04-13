import request from '@/utils/request';

// 查询用户列表
export function listUser(query?: any) {
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
export function addUser(data: any) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  });
}

// 修改用户
export function updateUser(data: any) {
  return request({
    url: '/system/user',
    method: 'put',
    data: data
  });
}

// 删除用户
export function delUser(userIds: string) {
  return request({
    url: '/system/user/' + userIds,
    method: 'delete'
  });
}