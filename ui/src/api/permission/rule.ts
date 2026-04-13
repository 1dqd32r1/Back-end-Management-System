import request from '@/utils/request';

// 动态规则API

export interface DynamicRule {
  ruleId: number;
  ruleName: string;
  ruleCode: string;
  ruleType: string;
  dimension: string;
  conditionExpression: string;
  actionType: string;
  priority: number;
  effectiveStart?: string;
  effectiveEnd?: string;
  status: number;
  description?: string;
  createdAt: string;
}

// 获取规则列表
export function getRuleList(params?: { ruleType?: string; status?: number }) {
  return request({
    url: '/api/permission/rule/list',
    method: 'get',
    params
  });
}

// 分页查询规则
export function getRulePage(params: { pageNum: number; pageSize: number; ruleType?: string }) {
  return request({
    url: '/api/permission/rule/page',
    method: 'get',
    params
  });
}

// 获取规则详情
export function getRule(ruleId: number) {
  return request({
    url: `/api/permission/rule/${ruleId}`,
    method: 'get'
  });
}

// 创建规则
export function createRule(data: DynamicRule) {
  return request({
    url: '/api/permission/rule/create',
    method: 'post',
    data
  });
}

// 更新规则
export function updateRule(ruleId: number, data: DynamicRule) {
  return request({
    url: `/api/permission/rule/${ruleId}`,
    method: 'put',
    data
  });
}

// 删除规则
export function deleteRule(ruleId: number) {
  return request({
    url: `/api/permission/rule/${ruleId}`,
    method: 'delete'
  });
}

// 启用规则
export function enableRule(ruleId: number) {
  return request({
    url: `/api/permission/rule/${ruleId}/enable`,
    method: 'post'
  });
}

// 禁用规则
export function disableRule(ruleId: number) {
  return request({
    url: `/api/permission/rule/${ruleId}/disable`,
    method: 'post'
  });
}

// 测试规则
export function testRule(data: { ruleId: number; testContext: Record<string, any> }) {
  return request({
    url: '/api/permission/rule/test',
    method: 'post',
    data
  });
}
