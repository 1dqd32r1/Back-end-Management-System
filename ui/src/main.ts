import { createApp, computed } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import "element-plus/dist/index.css";
import "element-plus/theme-chalk/dark/css-vars.css";
import "./style.css";
import { useAppStore } from "./store/modules/app";

// 注册 ECharts
import ECharts, { THEME_KEY } from 'vue-echarts';
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { BarChart, LineChart, PieChart, GaugeChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent
} from "echarts/components";

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  GaugeChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent
]);

const app = createApp(App);
const pinia = createPinia();
app.use(pinia);

const appStore = useAppStore();
appStore.initialize();

// 动态绑定 ECharts 主题
app.provide(THEME_KEY, computed(() => appStore.theme));

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.component('v-chart', ECharts);

app.use(router);
app.use(ElementPlus);
app.mount("#app");
