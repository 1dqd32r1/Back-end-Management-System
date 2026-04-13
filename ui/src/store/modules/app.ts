import { defineStore } from 'pinia';

type ThemeType = 'light' | 'dark';

type AppSettings = {
  theme: ThemeType;
  showLogo: boolean;
  tagsView: boolean;
  fixedHeader: boolean;
  emailNotify: boolean;
  systemNotify: boolean;
  twoFactor: boolean;
  timeout: number;
};

const STORAGE_KEY = 'app-settings';

const defaultSettings: AppSettings = {
  theme: 'light',
  showLogo: true,
  tagsView: true,
  fixedHeader: true,
  emailNotify: true,
  systemNotify: true,
  twoFactor: false,
  timeout: 30
};

export const useAppStore = defineStore('app', {
  state: (): AppSettings => ({ ...defaultSettings }),
  actions: {
    initialize() {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        try {
          const parsed = JSON.parse(raw) as Partial<AppSettings>;
          this.theme = parsed.theme === 'dark' ? 'dark' : 'light';
          this.showLogo = parsed.showLogo ?? defaultSettings.showLogo;
          this.tagsView = parsed.tagsView ?? defaultSettings.tagsView;
          this.fixedHeader = parsed.fixedHeader ?? defaultSettings.fixedHeader;
          this.emailNotify = parsed.emailNotify ?? defaultSettings.emailNotify;
          this.systemNotify = parsed.systemNotify ?? defaultSettings.systemNotify;
          this.twoFactor = parsed.twoFactor ?? defaultSettings.twoFactor;
          this.timeout = parsed.timeout ?? defaultSettings.timeout;
        } catch {
          this.resetSettings();
        }
      } else {
        this.persist();
      }
      this.applyTheme();
    },
    persist() {
      const payload: AppSettings = {
        theme: this.theme,
        showLogo: this.showLogo,
        tagsView: this.tagsView,
        fixedHeader: this.fixedHeader,
        emailNotify: this.emailNotify,
        systemNotify: this.systemNotify,
        twoFactor: this.twoFactor,
        timeout: this.timeout
      };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
    },
    setTheme(theme: ThemeType) {
      this.theme = theme;
      this.applyTheme();
      this.persist();
    },
    toggleTheme() {
      this.setTheme(this.theme === 'light' ? 'dark' : 'light');
    },
    updateSettings(payload: Partial<AppSettings>) {
      if (payload.theme) {
        this.theme = payload.theme;
      }
      if (typeof payload.showLogo === 'boolean') {
        this.showLogo = payload.showLogo;
      }
      if (typeof payload.tagsView === 'boolean') {
        this.tagsView = payload.tagsView;
      }
      if (typeof payload.fixedHeader === 'boolean') {
        this.fixedHeader = payload.fixedHeader;
      }
      if (typeof payload.emailNotify === 'boolean') {
        this.emailNotify = payload.emailNotify;
      }
      if (typeof payload.systemNotify === 'boolean') {
        this.systemNotify = payload.systemNotify;
      }
      if (typeof payload.twoFactor === 'boolean') {
        this.twoFactor = payload.twoFactor;
      }
      if (typeof payload.timeout === 'number') {
        this.timeout = Math.min(1440, Math.max(1, payload.timeout));
      }
      this.applyTheme();
      this.persist();
    },
    resetSettings() {
      this.theme = defaultSettings.theme;
      this.showLogo = defaultSettings.showLogo;
      this.tagsView = defaultSettings.tagsView;
      this.fixedHeader = defaultSettings.fixedHeader;
      this.emailNotify = defaultSettings.emailNotify;
      this.systemNotify = defaultSettings.systemNotify;
      this.twoFactor = defaultSettings.twoFactor;
      this.timeout = defaultSettings.timeout;
      this.applyTheme();
      this.persist();
    },
    applyTheme() {
      const html = document.documentElement;
      if (this.theme === 'dark') {
        html.classList.add('dark');
      } else {
        html.classList.remove('dark');
      }
    }
  }
});
