import request from '@/utils/request';

// 权限决策API

export interface DecisionRequest {
  userId: number;
  resourceType: string;
  resourceId?: string;
  operation: string;
  context?: Record<string, any>;
}

export interface DecisionResult {
  requestId: string;
  decision: 'ALLOW' | 'DENY' | 'CONDITIONAL';
  matchedRules: string[];
  denialReason?: string;
  explanation?: string;
  latencyMs: number;
}

// 实时权限检查
export function checkPermission(data: DecisionRequest) {
  return request({
    url: '/api/permission/decision/check',
    method: 'post',
    data
  });
}

// 批量权限检查
export function batchCheckPermission(data: { userId: number; checks: Array<{ resourceType: string; resourceId?: string; operation: string }> }) {
  return request({
    url: '/api/permission/decision/batch',
    method: 'post',
    data
  });
}

// 获取决策解释
export function getExplanation(requestId: string) {
  return request({
    url: `/api/permission/decision/explain/${requestId}`,
    method: 'get'
  });
}
