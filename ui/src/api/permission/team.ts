import request from '@/utils/request';

// 虚拟团队API

export interface VirtualTeam {
  teamId: number;
  teamName: string;
  teamCode: string;
  teamType: string;
  projectId?: number;
  parentDeptId?: number;
  inheritedPermissions?: string;
  exclusivePermissions?: string;
  leaderId?: number;
  memberCount: number;
  effectiveStart?: string;
  effectiveEnd?: string;
  status: number;
}

export interface VirtualTeamMember {
  teamId: number;
  userId: number;
  memberRole: string;
  joinTime: string;
}

// 获取虚拟团队列表
export function getTeamList(params?: { teamType?: string; status?: number }) {
  return request({
    url: '/api/permission/virtual-team/list',
    method: 'get',
    params
  });
}

// 获取团队详情
export function getTeam(teamId: number) {
  return request({
    url: `/api/permission/virtual-team/${teamId}`,
    method: 'get'
  });
}

// 创建虚拟团队
export function createTeam(data: { teamName: string; teamCode: string; teamType: string; projectId?: number; parentDeptId?: number; memberIds?: number[] }) {
  return request({
    url: '/api/permission/virtual-team/create',
    method: 'post',
    data
  });
}

// 更新团队信息
export function updateTeam(teamId: number, data: VirtualTeam) {
  return request({
    url: `/api/permission/virtual-team/${teamId}`,
    method: 'put',
    data
  });
}

// 解散团队
export function dissolveTeam(teamId: number) {
  return request({
    url: `/api/permission/virtual-team/${teamId}`,
    method: 'delete'
  });
}

// 获取团队成员
export function getTeamMembers(teamId: number) {
  return request({
    url: `/api/permission/virtual-team/${teamId}/members`,
    method: 'get'
  });
}

// 添加成员
export function addMembers(teamId: number, data: { userIds: number[]; role?: string }) {
  return request({
    url: `/api/permission/virtual-team/${teamId}/members`,
    method: 'post',
    data
  });
}

// 移除成员
export function removeMembers(teamId: number, data: { userIds: number[] }) {
  return request({
    url: `/api/permission/virtual-team/${teamId}/members`,
    method: 'delete',
    data
  });
}

// 设置负责人
export function setLeader(teamId: number, userId: number) {
  return request({
    url: `/api/permission/virtual-team/${teamId}/leader`,
    method: 'post',
    data: { userId }
  });
}

// 配置权限
export function configurePermissions(teamId: number, data: { inheritedPermissions?: string[]; exclusivePermissions?: string[] }) {
  return request({
    url: `/api/permission/virtual-team/${teamId}/permissions`,
    method: 'put',
    data
  });
}

// 获取用户的虚拟团队
export function getUserTeams(userId: number) {
  return request({
    url: `/api/permission/virtual-team/user/${userId}`,
    method: 'get'
  });
}
