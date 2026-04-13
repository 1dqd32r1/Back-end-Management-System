<template>
  <div class="errPage-container">
    <el-row>
      <el-col :span="12">
        <h1 class="text-jumbo text-ginormous">Oops!</h1>
        <h2>无法找到该页面</h2>
        <p>请检查您输入的网址是否正确，请点击以下按钮返回主页</p>
        <router-link to="/dashboard">
          <el-button type="primary" class="pan-back-btn">返回首页</el-button>
        </router-link>
      </el-col>
      <el-col :span="12">
        <img
          src="https://gw.alipayobjects.com/zos/rmsportal/KpPNEthWAVPwiNoNZSxe.svg"
          width="313"
          height="428"
          alt="404"
        />
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.errPage-container {
  width: 800px;
  max-width: 100%;
  margin: 100px auto;
  .text-jumbo {
    font-size: 60px;
    font-weight: 700;
    color: #484848;
  }
}
</style>
