import { defineStore } from 'pinia';
import { getToken, setToken, removeToken } from '@/utils/auth';
import request from '@/utils/request';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    name: '',
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
        }).catch(error => {
          reject(error);
        });
      });
    },
    getUserInfo() {
      request({
        url: '/common/getInfo',
        method: 'get'
      }).then((res: any) => {
        this.name = res.data.nickName || res.data.userName;
        this.avatar = res.data.avatar || '';
      });
    },
    logout() {
      this.token = '';
      removeToken();
    },
    setAvatar(avatar: string) {
      this.avatar = avatar;
    },
    setNickName(name: string) {
      this.name = name;
    }
  }
});