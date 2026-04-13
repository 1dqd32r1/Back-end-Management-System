import request from '@/utils/request';

/**
 * 预测下一个操作
 */
export function predictNextOperation(userId: number) {
  return request({
    url: `/api/permission/prediction/next-operation/${userId}`,
    method: 'get'
  });
}

/**
 * 预测访问模式
 */
export function predictAccessPattern(userId: number, hours: number = 24) {
  return request({
    url: `/api/permission/prediction/access-pattern/${userId}`,
    method: 'get',
    params: { hours }
  });
}

/**
 * 异常检测
 */
export function detectAnomaly(userId: number) {
  return request({
    url: `/api/permission/prediction/anomaly/${userId}`,
    method: 'get'
  });
}

/**
 * 权限使用趋势预测
 */
export function predictUsageTrend(userId: number, days: number = 7) {
  return request({
    url: `/api/permission/prediction/usage-trend/${userId}`,
    method: 'get',
    params: { days }
  });
}

/**
 * 训练预测模型
 */
export function trainModel(userId: number) {
  return request({
    url: `/api/permission/prediction/train/${userId}`,
    method: 'post'
  });
}
