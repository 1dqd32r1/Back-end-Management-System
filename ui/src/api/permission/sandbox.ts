import request from '@/utils/request';

/**
 * 执行What-If模拟
 */
export function executeWhatIf(data: any) {
  return request({
    url: '/api/permission/sandbox/what-if',
    method: 'post',
    data
  });
}

/**
 * 执行时光机模拟
 */
export function executeTimeTravel(data: any) {
  return request({
    url: '/api/permission/sandbox/time-travel',
    method: 'post',
    data
  });
}

/**
 * 执行压力测试
 */
export function executeStressTest(data: any) {
  return request({
    url: '/api/permission/sandbox/stress-test',
    method: 'post',
    data
  });
}

/**
 * 获取模拟历史
 */
export function getSimulationHistory(params: any) {
  return request({
    url: '/api/permission/sandbox/history',
    method: 'get',
    params
  });
}

/**
 * 获取影响面热力图
 */
export function getHeatmap(simulationId: number) {
  return request({
    url: `/api/permission/sandbox/heatmap/${simulationId}`,
    method: 'get'
  });
}
