import request from '@/utils/request';

// 熵值监控API

export interface EntropyMonitor {
  monitorId: number;
  userId: number;
  permissionEntropy: number;
  effectivePermissions: number;
  usedPermissions: number;
  unusedPermissions: number;
  driftScore: number;
  creepIndicators: string;
  recommendedRevocations: string;
  periodStart: string;
  periodEnd: string;
}

// 获取用户权限熵值
export function getEntropy(userId: number) {
  return request({
    url: `/api/permission/entropy/${userId}`,
    method: 'get'
  });
}

// 获取熵值历史
export function getEntropyHistory(userId: number, days: number = 30) {
  return request({
    url: `/api/permission/entropy/${userId}/history`,
    method: 'get',
    params: { days }
  });
}

// 权限漂移报告
export function getDriftReport(params: { pageNum: number; pageSize: number; threshold?: number }) {
  return request({
    url: '/api/permission/entropy/drift-report',
    method: 'get',
    params
  });
}

// 生成权限回收建议
export function recommendRevocation(userId: number) {
  return request({
    url: '/api/permission/entropy/recommend-revocation',
    method: 'post',
    data: { userId }
  });
}
