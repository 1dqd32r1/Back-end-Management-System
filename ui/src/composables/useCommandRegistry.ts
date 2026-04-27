import { ref, computed } from 'vue';

export interface Command {
  id: string;
  label: string;           // 显示名称，如 "去 用户管理"
  keywords: string[];      // 搜索关键词，如 ["用户", "user", "管理", "gl"]
  category: 'navigation' | 'action' | 'setting' | 'api';
  icon?: string;           // Element Plus icon 名称
  shortcut?: string;       // 快捷键显示，如 "Ctrl+Shift+U"
  action: () => void;      // 执行函数
  params?: CommandParam[]; // 参数定义
}

export interface CommandParam {
  key: string;
  label: string;
  type: 'text' | 'number' | 'select';
  required?: boolean;
  placeholder?: string;
  options?: { label: string; value: string | number }[];
}

interface CommandRegistry {
  registerCommand: (command: Command) => void;
  registerCommands: (commands: Command[]) => void;
  unregisterCommand: (id: string) => void;
  getAllCommands: () => Command[];
  search: (query: string) => Command[];
  getRecentCommands: () => Command[];
  addRecentCommand: (id: string) => void;
  clearRecentCommands: () => void;
}

const STORAGE_KEY = 'command-palette-recent';
const MAX_RECENT = 5;

// 全局状态
const commands = ref<Map<string, Command>>(new Map());
const recentIds = ref<string[]>([]);

// 初始化最近使用
const initRecent = () => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      recentIds.value = JSON.parse(stored);
    }
  } catch {
    recentIds.value = [];
  }
};

// 初始化
initRecent();

// 拼音首字母映射（简化版，用于中文搜索）
const pinyinMap: Record<string, string> = {
  '首': 'sy', '页': 'y', '用': 'y', '户': 'h', '管': 'gl', '理': 'l',
  '角': 'j', '色': 's', '通': 't', '知': 'z', '公': 'g', '告': 'g',
  '接': 'j', '口': 'k', '文': 'w', '档': 'd', '服': 'fw', '务': 'w',
  '监': 'jk', '控': 'k', '对': 'd', '话': 'h', '数': 's', '据': 'j',
  '城': 'c', '市': 's', '系': 'x', '统': 't', '设': 's', '置': 'z',
  '权': 'q', '限': 'x', '决': 'j', '策': 'c', '动': 'd', '态': 't',
  '规': 'g', '则': 'z', '行': 'x', '为': 'w', '分': 'f', '析': 'x',
  '熵': 's', '值': 'z', '审': 's', '计': 'j', '时': 's', '光': 'g',
  '机': 'j', '虚': 'x', '拟': 'n', '团': 't', '队': 'd', '沙': 's',
  '盒': 'h', '模': 'm', '个': 'g', '人': 'r', '信': 'x', '息': 'x',
  '添': 't', '加': 'j', '删': 's', '除': 'c', '修': 'x', '改': 'g',
  '查': 'c', '看': 'k', '导': 'd', '出': 'c', '入': 'r', '搜': 's',
  '索': 's', '重': 'c', '复': 'f', '制': 'z', '切': 'q', '换': 'h',
  '主': 'z', '题': 't', '亮': 'l', '暗': 'a', '注': 'z', '销': 'x',
  '登': 'd', '录': 'l', '退': 't', '启': 'q', '禁': 'j', '停': 't',
  '测': 'c', '试': 's', '运': 'y', '行': 'x', '压': 'y',
  '力': 'l', '创': 'c', '建': 'j', '编': 'b', '辑': 'j', '详': 'x',
  '情': 'q', '列': 'l', '表': 'b', '快': 'k', '照': 'z', '比': 'b',
  '较': 'j', '回': 'h', '放': 'f', '预': 'y', '演': 'y', '历': 'l',
  '史': 's', '报': 'b', '消': 'x', '耗': 'h', '安': 'a', '全': 'q',
  '警': 'j', '异': 'y', '常': 'c', '标': 'b', '签': 'q',
  '视': 's', '图': 't', '固': 'g', '定': 'd', '头': 't', '邮': 'y',
  '件': 'j', '通': 't', '双': 's', '因': 'y', '素': 's', '认': 'r',
  '验': 'y', '证': 'z', '超': 'c', '时': 's', '间': 'j', '隔': 'g',
  '清': 'q', '空': 'k', '缓': 'h', '存': 'c', '像': 'x',
  '名': 'm', '称': 'c', '密': 'm', '码': 'm'
};

// 获取拼音首字母
const getPinyinInitials = (text: string): string => {
  let result = '';
  for (const char of text) {
    if (pinyinMap[char]) {
      result += pinyinMap[char];
    }
  }
  return result;
};

export function useCommandRegistry(): CommandRegistry {
  const registerCommand = (command: Command) => {
    // 自动生成拼音首字母作为关键词
    const pinyinInitials = getPinyinInitials(command.label);
    const enhancedKeywords = new Set([
      ...command.keywords,
      pinyinInitials,
      command.label.toLowerCase()
    ]);
    commands.value.set(command.id, {
      ...command,
      keywords: [...enhancedKeywords]
    });
  };

  const registerCommands = (commandList: Command[]) => {
    commandList.forEach(registerCommand);
  };

  const unregisterCommand = (id: string) => {
    commands.value.delete(id);
  };

  const getAllCommands = (): Command[] => {
    return Array.from(commands.value.values());
  };

  const search = (query: string): Command[] => {
    if (!query.trim()) {
      return getAllCommands();
    }

    const lowerQuery = query.toLowerCase().trim();
    const queryPinyin = getPinyinInitials(query);

    return getAllCommands()
      .map(cmd => {
        let score = 0;

        // 完全匹配 label
        if (cmd.label.toLowerCase() === lowerQuery) {
          score = 100;
        }
        // label 开头匹配
        else if (cmd.label.toLowerCase().startsWith(lowerQuery)) {
          score = 80;
        }
        // label 包含匹配
        else if (cmd.label.toLowerCase().includes(lowerQuery)) {
          score = 60;
        }
        // 关键词匹配
        else {
          for (const keyword of cmd.keywords) {
            if (keyword.toLowerCase() === lowerQuery) {
              score = Math.max(score, 90);
            } else if (keyword.toLowerCase().startsWith(lowerQuery)) {
              score = Math.max(score, 70);
            } else if (keyword.toLowerCase().includes(lowerQuery)) {
              score = Math.max(score, 50);
            }
            // 拼音首字母匹配
            else if (queryPinyin && keyword.includes(queryPinyin)) {
              score = Math.max(score, 40);
            }
          }
        }

        return { command: cmd, score };
      })
      .filter(item => item.score > 0)
      .sort((a, b) => b.score - a.score)
      .map(item => item.command);
  };

  const getRecentCommands = (): Command[] => {
    const allCommands = getAllCommands();
    const commandMap = new Map(allCommands.map(cmd => [cmd.id, cmd]));
    return recentIds.value
      .map(id => commandMap.get(id))
      .filter((cmd): cmd is Command => cmd !== undefined);
  };

  const addRecentCommand = (id: string) => {
    recentIds.value = [
      id,
      ...recentIds.value.filter(i => i !== id)
    ].slice(0, MAX_RECENT);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(recentIds.value));
  };

  const clearRecentCommands = () => {
    recentIds.value = [];
    localStorage.removeItem(STORAGE_KEY);
  };

  return {
    registerCommand,
    registerCommands,
    unregisterCommand,
    getAllCommands,
    search,
    getRecentCommands,
    addRecentCommand,
    clearRecentCommands
  };
}
