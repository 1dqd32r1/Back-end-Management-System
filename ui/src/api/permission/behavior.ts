import request from '@/utils/request';

// 行为分析API

export interface UserProfile {
  profileId: number;
  userId: number;
  typicalAccessHours: string;
  typicalLocations: string;
  typicalDevices: string;
  frequentOperations: string;
  collaborationPatterns: string;
  skillTags: string;
  expertiseDomains: string;
  permissionUsageStats: string;
  unusedPermissions: string;
  overprivilegedScore: number;
  behaviorBaseline: string;
  lastBaselineUpdate: string;
}

export interface BehaviorEvent {
  eventId: number;
  userId: number;
  eventType: string;
  eventSource: string;
  resourceType: string;
  resourceId: string;
  operation: string;
  eventData: string;
  sessionId: string;
  ipAddress: string;
  userAgent: string;
  geoLocation: string;
  isAnomaly: number;
  anomalyType: string;
  processed: number;
  eventTime: string;
}

// 获取用户行为画像
export function getUserProfile(userId: number) {
  return request({
    url: `/api/permission/behavior/profile/${userId}`,
    method: 'get'
  });
}

// 获取行为事件列表
export function getBehaviorEvents(params: { userId?: number; eventType?: string; pageNum: number; pageSize: number }) {
  return request({
    url: '/api/permission/behavior/events',
    method: 'get',
    params
  });
}

// 获取异常行为列表
export function getAnomalyEvents(params: { userId?: number; limit?: number }) {
  return request({
    url: '/api/permission/behavior/anomaly',
    method: 'get',
    params
  });
}

// 获取行为统计
export function getBehaviorStats(userId: number) {
  return request({
    url: `/api/permission/behavior/stats/${userId}`,
    method: 'get'
  });
}

// 行为反馈
export function feedbackBehavior(data: { eventId: number; isAnomaly: boolean; comment?: string }) {
  return request({
    url: '/api/permission/behavior/feedback',
    method: 'post',
    data
  });
}
