import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import Layout from '../layout/index.vue';
import { getToken } from '@/utils/auth';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    component: () => import('../views/login.vue'),
    meta: { hidden: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: 'system/user',
        name: 'User',
        component: () => import('../views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('../views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'Postcard' }
      },
      {
        path: 'system/notice',
        name: 'Notice',
        component: () => import('../views/system/notice/index.vue'),
        meta: { title: '通知公告', icon: 'Bell' }
      },
      {
        path: 'system/profile',
        name: 'Profile',
        component: () => import('../views/system/profile/index.vue'),
        meta: { title: '个人信息', icon: 'User' },
        hidden: true
      },
      {
        path: 'monitor/server',
        name: 'Server',
        component: () => import('../views/monitor/server/index.vue'),
        meta: { title: '服务监控', icon: 'Monitor' }
      },
      {
        path: 'ai/chat',
        name: 'AiChat',
        component: () => import('../views/ai/chat/index.vue'),
        meta: { title: 'AI 对话', icon: 'ChatDotRound' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/settings/index.vue'),
        meta: { title: '系统设置', icon: 'Setting' }
      },
      // 智能权限模块
      {
        path: 'permission/decision',
        name: 'PermissionDecision',
        component: () => import('../views/permission/decision/index.vue'),
        meta: { title: '权限决策', icon: 'Key' }
      },
      {
        path: 'permission/rule',
        name: 'DynamicRule',
        component: () => import('../views/permission/rule/index.vue'),
        meta: { title: '动态规则', icon: 'Operation' }
      },
      {
        path: 'permission/behavior',
        name: 'BehaviorAnalysis',
        component: () => import('../views/permission/behavior/index.vue'),
        meta: { title: '行为分析', icon: 'DataLine' }
      },
      {
        path: 'permission/entropy',
        name: 'EntropyMonitor',
        component: () => import('../views/permission/entropy/index.vue'),
        meta: { title: '熵值监控', icon: 'TrendCharts' }
      },
      {
        path: 'permission/audit',
        name: 'AuditTimeMachine',
        component: () => import('../views/permission/audit/index.vue'),
        meta: { title: '审计时光机', icon: 'Clock' }
      },
      {
        path: 'permission/team',
        name: 'VirtualTeam',
        component: () => import('../views/permission/team/index.vue'),
        meta: { title: '虚拟团队', icon: 'UserFilled' }
      },
      {
        path: 'permission/sandbox',
        name: 'SandboxSimulator',
        component: () => import('../views/permission/sandbox/index.vue'),
        meta: { title: '沙盒模拟', icon: 'Monitor' }
      }
    ]
  },
  {
    path: '/404',
    component: () => import('../views/error/404.vue'),
    hidden: true
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    hidden: true
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局路由守卫
const whiteList = ['/login'];
router.beforeEach((to, from) => {
  if (getToken()) {
    if (to.path === '/login') {
      return { path: '/' };
    } else {
      return true;
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      return true;
    } else {
      return `/login?redirect=${to.fullPath}`;
    }
  }
});

export default router;