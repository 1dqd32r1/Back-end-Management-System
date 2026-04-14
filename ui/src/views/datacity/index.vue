<template>
  <div class="data-city-container">
    <!-- 3D画布 -->
    <div ref="canvasContainer" class="canvas-container"></div>

    <!-- 顶部统计信息 -->
    <div class="stats-panel">
      <div class="stat-item">
        <div class="stat-value">{{ formatNumber(statistics.totalSales) }}</div>
        <div class="stat-label">总销售额(万)</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ formatNumber(statistics.totalUsers) }}</div>
        <div class="stat-label">总用户数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ formatNumber(statistics.totalOrders) }}</div>
        <div class="stat-label">总订单数</div>
      </div>
      <div class="stat-item warning">
        <div class="stat-value">{{ statistics.abnormalCount }}</div>
        <div class="stat-label">异常区域</div>
      </div>
    </div>

    <!-- 图例 -->
    <div class="legend-panel">
      <div class="legend-title">图例说明</div>
      <div class="legend-item">
        <span class="legend-color" style="background: linear-gradient(to top, #1a237e, #4fc3f7)"></span>
        <span>建筑高度 = 销售额</span>
      </div>
      <div class="legend-item">
        <span class="legend-color" style="background: linear-gradient(to top, #b71c1c, #ff5252)"></span>
        <span>红色警示塔 = 异常数据</span>
      </div>
      <div class="legend-item">
        <span class="legend-color" style="background: linear-gradient(to right, #37474f, #78909c)"></span>
        <span>建筑面积 = 用户规模</span>
      </div>
    </div>

    <!-- 操作提示 -->
    <div class="tips-panel">
      <div class="tip">🖱️ 左键拖动旋转 | 滚轮缩放 | 右键平移</div>
      <div class="tip">👆 点击建筑查看详细数据</div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="selectedBuilding?.name" width="400px" class="detail-dialog">
      <div v-if="selectedBuilding" class="building-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="销售额">
            <span class="highlight">{{ selectedBuilding.sales }} 万</span>
          </el-descriptions-item>
          <el-descriptions-item label="用户增长">
            <span class="highlight">{{ formatNumber(selectedBuilding.userGrowth) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单数量">
            {{ selectedBuilding.details?.orderCount }}
          </el-descriptions-item>
          <el-descriptions-item label="平均客单价">
            ¥{{ selectedBuilding.details?.avgOrderValue?.toFixed(2) }}
          </el-descriptions-item>
          <el-descriptions-item label="转化率">
            {{ selectedBuilding.details?.conversionRate?.toFixed(2) }}%
          </el-descriptions-item>
          <el-descriptions-item label="退货率">
            :class="{'text-danger': selectedBuilding.details?.returnRate > 5}"
            {{ selectedBuilding.details?.returnRate?.toFixed(2) }}%
          </el-descriptions-item>
          <el-descriptions-item label="热门产品">
            <el-tag v-for="p in selectedBuilding.details?.topProducts" :key="p" size="small" class="mr-1">{{ p }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedBuilding.isAbnormal ? 'danger' : 'success'">
              {{ selectedBuilding.isAbnormal ? '⚠️ 异常' : '✓ 正常' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 加载提示 -->
    <div v-if="loading" class="loading-overlay">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <div>加载3D城市中...</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { Loading } from '@element-plus/icons-vue';
import request from '@/utils/request';

interface Building {
  id: number;
  name: string;
  sales: number;
  height: number;
  userGrowth: number;
  width: number;
  isAbnormal: boolean;
  color: string;
  details: {
    orderCount: number;
    avgOrderValue: number;
    conversionRate: number;
    returnRate: number;
    topProducts: string[];
  };
}

interface Statistics {
  totalSales: number;
  totalUsers: number;
  totalOrders: number;
  abnormalCount: number;
}

const canvasContainer = ref<HTMLDivElement>();
const loading = ref(true);
const detailVisible = ref(false);
const selectedBuilding = ref<Building | null>(null);
const statistics = ref<Statistics>({
  totalSales: 0,
  totalUsers: 0,
  totalOrders: 0,
  abnormalCount: 0
});

let scene: THREE.Scene;
let camera: THREE.PerspectiveCamera;
let renderer: THREE.WebGLRenderer;
let controls: OrbitControls;
let buildings: THREE.Mesh[] = [];
let buildingData: Building[] = [];
let raycaster: THREE.Raycaster;
let mouse: THREE.Vector2;
let animationId: number;

const formatNumber = (num: number | undefined) => {
  if (!num) return '0';
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万';
  }
  return num.toLocaleString();
};

const initThreeJS = async () => {
  if (!canvasContainer.value) return;

  // 获取数据
  try {
    const res = await request.get('/datacity/data');
    if (res.data) {
      buildingData = res.data.buildings || [];
      statistics.value = res.data.statistics || statistics.value;
    }
  } catch (e) {
    console.error('获取数据失败', e);
  }

  const width = canvasContainer.value.clientWidth;
  const height = canvasContainer.value.clientHeight;

  // 创建场景
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x0a1628);
  scene.fog = new THREE.Fog(0x0a1628, 50, 200);

  // 创建相机
  camera = new THREE.PerspectiveCamera(60, width / height, 0.1, 1000);
  camera.position.set(30, 25, 30);

  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setSize(width, height);
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.shadowMap.enabled = true;
  renderer.shadowMap.type = THREE.PCFSoftShadowMap;
  canvasContainer.value.appendChild(renderer.domElement);

  // 添加控制器
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  controls.minDistance = 10;
  controls.maxDistance = 100;
  controls.maxPolarAngle = Math.PI / 2.2;

  // 射线检测
  raycaster = new THREE.Raycaster();
  mouse = new THREE.Vector2();

  // 添加灯光
  addLights();

  // 创建地面
  createGround();

  // 创建建筑
  createBuildings();

  // 创建装饰
  createDecorations();

  loading.value = false;

  // 添加事件监听
  window.addEventListener('resize', onWindowResize);
  renderer.domElement.addEventListener('click', onMouseClick);

  // 开始动画
  animate();
};

const addLights = () => {
  // 环境光
  const ambientLight = new THREE.AmbientLight(0x404060, 0.5);
  scene.add(ambientLight);

  // 主方向光
  const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
  directionalLight.position.set(20, 30, 20);
  directionalLight.castShadow = true;
  directionalLight.shadow.mapSize.width = 2048;
  directionalLight.shadow.mapSize.height = 2048;
  directionalLight.shadow.camera.near = 0.5;
  directionalLight.shadow.camera.far = 100;
  directionalLight.shadow.camera.left = -50;
  directionalLight.shadow.camera.right = 50;
  directionalLight.shadow.camera.top = 50;
  directionalLight.shadow.camera.bottom = -50;
  scene.add(directionalLight);

  // 蓝色点光源
  const blueLight = new THREE.PointLight(0x4fc3f7, 1, 50);
  blueLight.position.set(-15, 10, -15);
  scene.add(blueLight);

  // 橙色点光源
  const orangeLight = new THREE.PointLight(0xffb74d, 0.8, 50);
  orangeLight.position.set(15, 10, 15);
  scene.add(orangeLight);
};

const createGround = () => {
  // 地面网格
  const gridHelper = new THREE.GridHelper(100, 50, 0x1a3a5c, 0x0d2137);
  scene.add(gridHelper);

  // 地面平面
  const groundGeometry = new THREE.PlaneGeometry(100, 100);
  const groundMaterial = new THREE.MeshStandardMaterial({
    color: 0x0a1628,
    roughness: 0.8,
    metalness: 0.2,
  });
  const ground = new THREE.Mesh(groundGeometry, groundMaterial);
  ground.rotation.x = -Math.PI / 2;
  ground.position.y = -0.01;
  ground.receiveShadow = true;
  scene.add(ground);
};

const createBuildings = () => {
  const gridSize = Math.ceil(Math.sqrt(buildingData.length));
  const spacing = 6;

  buildingData.forEach((data, index) => {
    const row = Math.floor(index / gridSize);
    const col = index % gridSize;

    const x = (col - gridSize / 2) * spacing;
    const z = (row - gridSize / 2) * spacing;

    // 建筑尺寸
    const width = 2 + data.width;
    const height = 2 + data.height;
    const depth = 2 + data.width;

    // 创建建筑主体
    const geometry = new THREE.BoxGeometry(width, height, depth);

    // 创建渐变材质
    let material: THREE.MeshStandardMaterial;
    if (data.isAbnormal) {
      material = new THREE.MeshStandardMaterial({
        color: 0xff4444,
        emissive: 0xff0000,
        emissiveIntensity: 0.3,
        roughness: 0.3,
        metalness: 0.7,
      });
    } else {
      material = new THREE.MeshStandardMaterial({
        color: new THREE.Color(data.color),
        roughness: 0.3,
        metalness: 0.6,
      });
    }

    const building = new THREE.Mesh(geometry, material);
    building.position.set(x, height / 2, z);
    building.castShadow = true;
    building.receiveShadow = true;
    (building as any).userData = data;

    buildings.push(building);
    scene.add(building);

    // 添加发光效果
    if (data.isAbnormal) {
      // 红色警示光环
      const ringGeometry = new THREE.RingGeometry(width * 0.8, width, 32);
      const ringMaterial = new THREE.MeshBasicMaterial({
        color: 0xff0000,
        side: THREE.DoubleSide,
        transparent: true,
        opacity: 0.6,
      });
      const ring = new THREE.Mesh(ringGeometry, ringMaterial);
      ring.rotation.x = -Math.PI / 2;
      ring.position.set(x, 0.1, z);
      scene.add(ring);
    }

    // 添加建筑顶部装饰
    const topGeometry = new THREE.BoxGeometry(width * 0.3, 0.5, depth * 0.3);
    const topMaterial = new THREE.MeshStandardMaterial({
      color: data.isAbnormal ? 0xff6666 : 0x88ccff,
      emissive: data.isAbnormal ? 0xff0000 : 0x4fc3f7,
      emissiveIntensity: 0.5,
    });
    const top = new THREE.Mesh(topGeometry, topMaterial);
    top.position.set(x, height + 0.25, z);
    scene.add(top);

    // 添加标签（简化版）
    const canvas = document.createElement('canvas');
    canvas.width = 128;
    canvas.height = 64;
    const ctx = canvas.getContext('2d')!;
    ctx.fillStyle = data.isAbnormal ? '#ff4444' : '#4fc3f7';
    ctx.font = 'bold 20px Arial';
    ctx.textAlign = 'center';
    ctx.fillText(data.name, 64, 25);
    ctx.font = '16px Arial';
    ctx.fillText(`${data.sales}万`, 64, 50);

    const texture = new THREE.CanvasTexture(canvas);
    const spriteMaterial = new THREE.SpriteMaterial({ map: texture, transparent: true });
    const sprite = new THREE.Sprite(spriteMaterial);
    sprite.position.set(x, height + 2.5, z);
    sprite.scale.set(4, 2, 1);
    scene.add(sprite);
  });
};

const createDecorations = () => {
  // 添加粒子效果 - 模拟数据流动
  const particlesGeometry = new THREE.BufferGeometry();
  const particleCount = 500;
  const positions = new Float32Array(particleCount * 3);
  const colors = new Float32Array(particleCount * 3);

  for (let i = 0; i < particleCount; i++) {
    positions[i * 3] = (Math.random() - 0.5) * 80;
    positions[i * 3 + 1] = Math.random() * 30;
    positions[i * 3 + 2] = (Math.random() - 0.5) * 80;

    const color = new THREE.Color();
    color.setHSL(0.55 + Math.random() * 0.1, 0.8, 0.6);
    colors[i * 3] = color.r;
    colors[i * 3 + 1] = color.g;
    colors[i * 3 + 2] = color.b;
  }

  particlesGeometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
  particlesGeometry.setAttribute('color', new THREE.BufferAttribute(colors, 3));

  const particlesMaterial = new THREE.PointsMaterial({
    size: 0.15,
    vertexColors: true,
    transparent: true,
    opacity: 0.8,
  });

  const particles = new THREE.Points(particlesGeometry, particlesMaterial);
  scene.add(particles);

  // 添加道路网格线
  const lineMaterial = new THREE.LineBasicMaterial({
    color: 0x1a3a5c,
    transparent: true,
    opacity: 0.5,
  });

  for (let i = -50; i <= 50; i += 10) {
    const points1 = [new THREE.Vector3(i, 0.02, -50), new THREE.Vector3(i, 0.02, 50)];
    const geometry1 = new THREE.BufferGeometry().setFromPoints(points1);
    const line1 = new THREE.Line(geometry1, lineMaterial);
    scene.add(line1);

    const points2 = [new THREE.Vector3(-50, 0.02, i), new THREE.Vector3(50, 0.02, i)];
    const geometry2 = new THREE.BufferGeometry().setFromPoints(points2);
    const line2 = new THREE.Line(geometry2, lineMaterial);
    scene.add(line2);
  }
};

const onMouseClick = (event: MouseEvent) => {
  if (!canvasContainer.value) return;

  const rect = canvasContainer.value.getBoundingClientRect();
  mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
  mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

  raycaster.setFromCamera(mouse, camera);
  const intersects = raycaster.intersectObjects(buildings);

  if (intersects.length > 0) {
    const building = intersects[0].object;
    selectedBuilding.value = (building as any).userData as Building;
    detailVisible.value = true;
  }
};

const onWindowResize = () => {
  if (!canvasContainer.value) return;

  const width = canvasContainer.value.clientWidth;
  const height = canvasContainer.value.clientHeight;

  camera.aspect = width / height;
  camera.updateProjectionMatrix();
  renderer.setSize(width, height);
};

const animate = () => {
  animationId = requestAnimationFrame(animate);

  controls.update();

  // 建筑呼吸动画
  buildings.forEach((building, index) => {
    const data = (building as any).userData;
    if (data.isAbnormal) {
      building.position.y = (2 + data.height / 2) + Math.sin(Date.now() * 0.003 + index) * 0.2;
    }
  });

  renderer.render(scene, camera);
};

onMounted(() => {
  initThreeJS();
});

onUnmounted(() => {
  window.removeEventListener('resize', onWindowResize);
  if (renderer) {
    renderer.domElement.removeEventListener('click', onMouseClick);
  }
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  if (renderer) {
    renderer.dispose();
  }
});
</script>

<style scoped>
.data-city-container {
  width: 100%;
  height: calc(100vh - 84px);
  position: relative;
  overflow: hidden;
  background: #0a1628;
}

.canvas-container {
  width: 100%;
  height: 100%;
}

.stats-panel {
  position: absolute;
  top: 20px;
  left: 20px;
  display: flex;
  gap: 15px;
  z-index: 10;
}

.stat-item {
  background: rgba(10, 22, 40, 0.9);
  border: 1px solid rgba(79, 195, 247, 0.3);
  border-radius: 8px;
  padding: 12px 20px;
  text-align: center;
  backdrop-filter: blur(10px);
}

.stat-item.warning {
  border-color: rgba(255, 68, 68, 0.5);
  background: rgba(255, 68, 68, 0.1);
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #4fc3f7;
}

.stat-item.warning .stat-value {
  color: #ff4444;
}

.stat-label {
  font-size: 12px;
  color: #90a4ae;
  margin-top: 4px;
}

.legend-panel {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(10, 22, 40, 0.9);
  border: 1px solid rgba(79, 195, 247, 0.3);
  border-radius: 8px;
  padding: 15px;
  z-index: 10;
  backdrop-filter: blur(10px);
}

.legend-title {
  font-size: 14px;
  font-weight: bold;
  color: #4fc3f7;
  margin-bottom: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #b0bec5;
}

.legend-color {
  width: 24px;
  height: 12px;
  border-radius: 2px;
}

.tips-panel {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(10, 22, 40, 0.9);
  border: 1px solid rgba(79, 195, 247, 0.3);
  border-radius: 8px;
  padding: 10px 20px;
  z-index: 10;
  backdrop-filter: blur(10px);
  text-align: center;
}

.tip {
  font-size: 12px;
  color: #90a4ae;
  margin: 4px 0;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(10, 22, 40, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
  color: #4fc3f7;
}

.loading-icon {
  font-size: 48px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.building-detail {
  padding: 10px 0;
}

.highlight {
  font-size: 18px;
  font-weight: bold;
  color: #4fc3f7;
}

.text-danger {
  color: #ff4444;
}

.mr-1 {
  margin-right: 4px;
}

:deep(.el-dialog) {
  background: rgba(15, 30, 50, 0.95);
  border: 1px solid rgba(79, 195, 247, 0.3);
}

:deep(.el-dialog__header) {
  background: rgba(79, 195, 247, 0.1);
  border-bottom: 1px solid rgba(79, 195, 247, 0.2);
}

:deep(.el-dialog__title) {
  color: #4fc3f7;
}

:deep(.el-descriptions) {
  background: transparent;
}

:deep(.el-descriptions__label) {
  color: #90a4ae;
  background: rgba(79, 195, 247, 0.05);
}

:deep(.el-descriptions__content) {
  color: #e0e0e0;
  background: rgba(0, 0, 0, 0.2);
}
</style>
