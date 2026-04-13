<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="clearfix">
          <span
            ><el-icon><Bell /></el-icon> 通知公告管理</span
          >
        </div>
      </template>
      <el-table :data="noticeList" style="width: 100%">
        <el-table-column label="公告标题" prop="noticeTitle" :show-overflow-tooltip="true" />
        <el-table-column label="公告类型" prop="noticeType" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.noticeType === '1' ? 'warning' : 'success'">
              {{ scope.row.noticeType === "1" ? "通知" : "公告" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === "0" ? "正常" : "关闭" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建者" align="center" prop="createBy" width="100" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
              >修改</el-button
            >
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const noticeList = ref([
  {
    noticeId: 1,
    noticeTitle: "关于系统维护的通知",
    noticeType: "1",
    status: "0",
    createBy: "admin",
    createTime: "2024-04-01 10:00:00",
  },
  {
    noticeId: 2,
    noticeTitle: "新功能上线公告",
    noticeType: "2",
    status: "0",
    createBy: "admin",
    createTime: "2024-03-25 15:30:00",
  },
]);

function handleUpdate(row: any) {
  ElMessage.success("修改成功 (模拟)");
}

function handleDelete(row: any) {
  ElMessageBox.confirm("确定删除此公告吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("删除成功 (模拟)");
    })
    .catch(() => {});
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
