import request from '@/utils/request';

export function getServerMonitor() {
  return request({
    url: '/monitor/server',
    method: 'get'
  });
}
