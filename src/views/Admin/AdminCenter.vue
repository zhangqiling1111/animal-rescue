<template>
  <div class="container">
    <div class="menu">
      <el-menu default-active="2" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose" router>
        <h2>管理中心</h2>
        <el-menu-item index="/admin/AdminCenter">
          <el-icon>
            <User />
          </el-icon>
          <template #title>个人信息</template>
        </el-menu-item>
        <el-menu-item index="/admin/AdminCenter/RescuestationApply">
          <el-icon>
            <List />
          </el-icon>
          <template #title>救助站审核</template>
        </el-menu-item>
        <el-menu-item index="/admin/AdminCenter/Rescuestation">
          <img class="nav-icon" src="../../assets/救助站user.png" />
          <template #title>救助站信息</template>
        </el-menu-item>
        <el-menu-item index="/admin/AdminCenter/UserManger">
          <img class="nav-icon" src="../../assets/用户管理.png" />
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="content">
      <!-- 下边内容 -->
      <router-view></router-view>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted } from 'vue'

import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'


const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}


</script>
<style scoped lang="scss">
  .container {
    height: 100vh;
    display: flex;
    // min-width: 1280px;
    max-width: 100%;
    margin: 0 auto;
    background-color: #ffffff; /* 主背景色改为白色 */
  }

  .menu {
    width: 200px;
    box-sizing: border-box;
    height: 100vh;
    background-color: #f8f8f8; /* 菜单背景色，稍微浅于白色以增加层次感 */
  }
  .menu .el-menu {
    height: 100%;
    width: 200px;
    padding-top: 30px;
  }
  .menu .el-menu .el-menu-item {
    // padding-left: 30px;
    color: #333333; /* 菜单项字体颜色，深灰色 */
  }
  .menu .el-menu .el-menu-item:hover, .menu .el-menu .el-menu-item.is-active {
    background-color: #cce5ff; /* 菜单项悬停或激活时的背景色，浅蓝色 */
    color: #007bff; /* 菜单项悬停或激活时的字体颜色，蓝色 */
  }
  .menu .el-menu .el-icon {
    // size: 60px;
    font-size: 24px;
    color: #007bff; /* 菜单项图标颜色，蓝色 */
  }
  .nav-icon {
    width: 24px;
    height: 24px;
    color: #007bff;
    margin-right: 6px;
  }
  .content {
    overflow: auto;
    padding: 0 40px;
    flex-grow: 1;
    // background-color: #ffffff; /* 保持与主容器相同的背景色 */
    background: linear-gradient(180deg, #e5f3fe, #ffffff);
  }

  h2 {
    margin-left: 45px;
    margin-top: 0;
    color: #007bff; /* 标题颜色改为蓝色 */
  }
  :deep(.el-dialog) {
    background: linear-gradient(180deg, #e5f3fe, #ffffff);
    border-radius: 12px;
  }
</style>