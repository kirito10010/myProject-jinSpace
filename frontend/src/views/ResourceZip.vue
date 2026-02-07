<template>
  <div class="resource-page">
    <h1>压缩文件</h1>
    <div class="resource-content">
      <el-empty description="暂无压缩文件资源" />
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.resource-page {
  width: 100%;
  height: 100%;
  padding: 20px;
}

.resource-page h1 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.resource-content {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
}
</style>
