import { useCommandRegistry, type Command } from '@/composables/useCommandRegistry';
import router from '@/router';
import { useAppStore } from '@/store/modules/app';
import { useUserStore } from '@/store/modules/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import { delUser } from '@/api/system/user';

export function registerCommands() {
  const { registerCommands } = useCommandRegistry();

  const commands: Command[] = [
    // ========== 导航命令 ==========
    {
      id: 'nav-dashboard',
      label: '去 首页',
      keywords: ['首页', 'dashboard', 'home'],
      category: 'navigation',
      icon: 'House',
      action: () => router.push('/dashboard')
    },
    {
      id: 'nav-user',
      label: '去 用户管理',
      keywords: ['用户', 'user', '管理', 'gl'],
      category: 'navigation',
      icon: 'User',
      action: () => router.push('/system/user')
    },
    {
      id: 'nav-role',
      label: '去 角色管理',
      keywords: ['角色', 'role', '管理', 'gl'],
      category: 'navigation',
      icon: 'Postcard',
      action: () => router.push('/system/role')
    },
    {
      id: 'nav-notice',
      label: '去 通知公告',
      keywords: ['通知', 'notice', '公告', 'gg'],
      category: 'navigation',
      icon: 'Bell',
      action: () => router.push('/system/notice')
    },
    {
      id: 'nav-swagger',
      label: '去 接口文档',
      keywords: ['接口', 'swagger', 'api', '文档', 'wd'],
      category: 'navigation',
      icon: 'Document',
      action: () => router.push('/system/swagger')
    },
    {
      id: 'nav-profile',
      label: '去 个人信息',
      keywords: ['个人', 'profile', '信息', 'xx'],
      category: 'navigation',
      icon: 'User',
      action: () => router.push('/system/profile')
    },
    {
      id: 'nav-server',
      label: '去 服务监控',
      keywords: ['服务', 'server', '监控', 'jk'],
      category: 'navigation',
      icon: 'Monitor',
      action: () => router.push('/monitor/server')
    },
    {
      id: 'nav-ai-chat',
      label: '去 AI对话',
      keywords: ['AI', 'ai', '对话', 'chat', 'dh'],
      category: 'navigation',
      icon: 'ChatDotRound',
      action: () => router.push('/ai/chat')
    },
    {
      id: 'nav-datacity',
      label: '去 3D数据城市',
      keywords: ['数据', 'datacity', '城市', '3d', 'cs'],
      category: 'navigation',
      icon: 'OfficeBuilding',
      action: () => router.push('/datacity')
    },
    {
      id: 'nav-settings',
      label: '去 系统设置',
      keywords: ['设置', 'settings', '系统', 'xt'],
      category: 'navigation',
      icon: 'Setting',
      action: () => router.push('/settings')
    },
    // 智能权限模块
    {
      id: 'nav-permission-decision',
      label: '去 权限决策',
      keywords: ['权限', 'permission', '决策', 'jc'],
      category: 'navigation',
      icon: 'Key',
      action: () => router.push('/permission/decision')
    },
    {
      id: 'nav-permission-rule',
      label: '去 动态规则',
      keywords: ['动态', 'dynamic', '规则', 'rule', 'gz'],
      category: 'navigation',
      icon: 'Operation',
      action: () => router.push('/permission/rule')
    },
    {
      id: 'nav-permission-behavior',
      label: '去 行为分析',
      keywords: ['行为', 'behavior', '分析', 'fx'],
      category: 'navigation',
      icon: 'DataLine',
      action: () => router.push('/permission/behavior')
    },
    {
      id: 'nav-permission-entropy',
      label: '去 熵值监控',
      keywords: ['熵值', 'entropy', '监控', 'jk'],
      category: 'navigation',
      icon: 'TrendCharts',
      action: () => router.push('/permission/entropy')
    },
    {
      id: 'nav-permission-audit',
      label: '去 审计时光机',
      keywords: ['审计', 'audit', '时光机', 'sgj'],
      category: 'navigation',
      icon: 'Clock',
      action: () => router.push('/permission/audit')
    },
    {
      id: 'nav-permission-team',
      label: '去 虚拟团队',
      keywords: ['虚拟', 'virtual', '团队', 'team', 'td'],
      category: 'navigation',
      icon: 'UserFilled',
      action: () => router.push('/permission/team')
    },
    {
      id: 'nav-permission-sandbox',
      label: '去 沙盒模拟',
      keywords: ['沙盒', 'sandbox', '模拟', 'mn'],
      category: 'navigation',
      icon: 'Monitor',
      action: () => router.push('/permission/sandbox')
    },

    // ========== 操作命令 ==========
    {
      id: 'action-add-user',
      label: '新增用户',
      keywords: ['新增', 'add', '用户', 'user', '添加', 'tj'],
      category: 'action',
      icon: 'Plus',
      action: () => router.push('/system/user')
    },
    {
      id: 'action-refresh',
      label: '刷新页面',
      keywords: ['刷新', 'refresh', 'reload', 'sx'],
      category: 'action',
      icon: 'Refresh',
      action: () => location.reload()
    },

    // ========== 设置命令 ==========
    {
      id: 'setting-toggle-theme',
      label: '切换主题',
      keywords: ['主题', 'theme', '切换', 'qh', 'dark', 'light', '亮', '暗'],
      category: 'setting',
      icon: 'Sunny',
      action: () => {
        const appStore = useAppStore();
        appStore.toggleTheme();
        ElMessage.success(`已切换至${appStore.theme === 'dark' ? '暗色' : '亮色'}主题`);
      }
    },
    {
      id: 'setting-reset',
      label: '重置设置',
      keywords: ['重置', 'reset', '设置', 'sz'],
      category: 'setting',
      icon: 'RefreshRight',
      action: () => {
        ElMessageBox.confirm('确定要重置所有设置为默认值吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const appStore = useAppStore();
          appStore.resetSettings();
          ElMessage.success('设置已重置');
        }).catch(() => {});
      }
    },
    {
      id: 'setting-logout',
      label: '退出登录',
      keywords: ['退出', 'logout', '登出', 'dc', '注销', 'zx'],
      category: 'setting',
      icon: 'SwitchButton',
      action: () => {
        ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const userStore = useUserStore();
          userStore.logout();
          router.push('/login');
          ElMessage.success('已退出登录');
        }).catch(() => {});
      }
    }
  ];

  registerCommands(commands);
}
