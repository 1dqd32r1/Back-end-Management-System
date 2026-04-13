import request from '@/utils/request';

// 审计时光机API

export interface AuditSnapshot {
  auditId: number;
  userId: number;
  snapshotTime: string;
  roles: string;
  permissions: string;
  entityPermissions: string;
  dataScope: string;
  changeEventId: number;
  changeType: string;
  changeReason: string;
}

// 获取用户权限快照列表
export function getSnapshots(userId: number, startTime?: string, endTime?: string) {
  return request({
    url: `/api/permission/audit/snapshot/${userId}`,
    method: 'get',
    params: { startTime, endTime }
  });
}

// 获取权限变更时间线
export function getTimeline(userId: number, startTime?: string, endTime?: string) {
  return request({
    url: `/api/permission/audit/timeline/${userId}`,
    method: 'get',
    params: { startTime, endTime }
  });
}

// 权限状态回放
export function replay(data: { userId: number; timestamp: string }) {
  return request({
    url: '/api/permission/audit/replay',
    method: 'post',
    data
  });
}

// 权限状态对比
export function compare(data: { userId: number; timestamp1: string; timestamp2: string }) {
  return request({
    url: '/api/permission/audit/compare',
    method: 'post',
    data
  });
}

// What-If 模拟
export function whatIf(data: { userId: number; changeType: string; changeData: Record<string, any> }) {
  return request({
    url: '/api/permission/audit/what-if',
    method: 'post',
    data
  });
}

// 创建快照
export function createSnapshot(data: { userId: number; changeType: string; changeReason: string }) {
  return request({
    url: '/api/permission/audit/snapshot/create',
    method: 'post',
    data
  });
}
