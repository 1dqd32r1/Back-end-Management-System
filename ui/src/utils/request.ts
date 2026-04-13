import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getToken, removeToken } from '@/utils/auth';

const service = axios.create({
  baseURL: 'http://localhost:8080', // 指向 Spring Boot 后端
  timeout: 10000
});

// request interceptor
service.interceptors.request.use(
  config => {
    // 可以在这里添加 Token 等请求头
    if (getToken()) {
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    return config;
  },
  error => {
    console.log(error);
    return Promise.reject(error);
  }
);

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data;
    // 这里与后端的 Result.java 对应，code === 200 为成功
    if (res.code === 401) {
      ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        removeToken();
        location.href = '/login';
      })
      return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
    } else if (res.code !== 200) {
      ElMessage({
        message: res.msg || '系统错误',
        type: 'error',
        duration: 5 * 1000
      });
      return Promise.reject(new Error(res.msg || 'Error'));
    } else {
      return res;
    }
  },
  error => {
    console.log('err' + error);
    let { message } = error;
    if (message == "Network Error") {
      message = "后端接口连接异常";
    } else if (message.includes("timeout")) {
      message = "系统接口请求超时";
    } else if (message.includes("Request failed with status code")) {
      if (error.response && error.response.status === 403) {
        message = "认证失败，请检查账号密码";
      } else {
        message = "系统接口" + message.substring(message.length - 3) + "异常";
      }
    }
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    });
    return Promise.reject(error);
  }
);

export default service;