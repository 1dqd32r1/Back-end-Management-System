import { defineStore } from 'pinia';
import { getToken, setToken, removeToken } from '@/utils/auth';
import request from '@/utils/request';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userId: 0,
    name: '',
    username: '',
    avatar: ''
  }),
  actions: {
    login(userInfo: any) {
      return new Promise((resolve, reject) => {
        request({
          url: '/login',
          method: 'post',
          data: userInfo
        }).then((res: any) => {
          this.token = res.data.token;
          setToken(res.data.token);
          // 登录成功后获取用户信息
          this.getUserInfo();
          resolve(res);
        }).catch((error: any) => {
          reject(error);
        });
      });
    },
    getUserInfo() {
      return new Promise((resolve, reject) => {
        request({
          url: '/common/getInfo',
          method: 'get'
        }).then((res: any) => {
          this.userId = res.data.userId;
          this.name = res.data.nickName || res.data.userName;
          this.username = res.data.userName;
          this.avatar = res.data.avatar || '';
          resolve(res);
        }).catch((error: any) => {
          reject(error);
        });
      });
    },
    logout() {
      this.token = '';
      this.userId = 0;
      this.name = '';
      this.username = '';
      removeToken();
    },
    setAvatar(avatar: string) {
      this.avatar = avatar;
    },
    setNickName(name: string) {
      this.name = name;
    },
    getUserId() {
      return this.userId;
    }
  }
});