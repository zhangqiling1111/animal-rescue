<template>
  <div class="shelter-home">
    <div class="header">
      <div class="header-content">
        <div class="welcome-section">
          <h1>欢迎回来，{{ username }}</h1>
          <p class="subtitle">这里是您的救助站管理平台</p>
        </div>
        <div class="user-info">
          <el-dropdown>
            <span class="user-dropdown">
              <el-avatar :size="40" :src="userAvatar" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToPersonalCenter">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-menu 
        :default-active="$route.path" 
        router
        class="shelter-menu"
        :collapse="isCollapse"
      >
        <div class="menu-header">
          <img src="@/assets/logo.png" alt="Logo" class="logo" />
          <span v-if="!isCollapse" class="menu-title">救助站管理</span>
        </div>
        
        <el-menu-item index="/home/shelter/animals">
          <el-icon><Collection /></el-icon>
          <template #title>动物列表</template>
        </el-menu-item>
        
        <el-menu-item index="/home/shelter/add-animal">
          <el-icon><Plus /></el-icon>
          <template #title>添加动物</template>
        </el-menu-item>
        
        <el-menu-item index="/home/shelter/rescue-records">
          <el-icon><Document /></el-icon>
          <template #title>救助记录</template>
        </el-menu-item>
        
        <el-menu-item index="/home/shelter/donation-records">
          <el-icon><Money /></el-icon>
          <template #title>捐赠记录</template>
        </el-menu-item>
        
        <el-menu-item index="/home/shelter/adoption-applications">
          <el-icon><Check /></el-icon>
          <template #title>领养申请审核</template>
        </el-menu-item>
        
        <el-menu-item index="/home/shelter/rescue-management">
          <el-icon><FirstAidKit /></el-icon>
          <template #title>救助上报管理</template>
        </el-menu-item>
        
        <el-menu-item index="/home/shelter/personal-center">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>

      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Collection, 
  Plus, 
  Document, 
  Money, 
  User, 
  SwitchButton,
  ArrowDown,
  Check,
  FirstAidKit
} from '@element-plus/icons-vue';

const router = useRouter();
const username = localStorage.getItem('username') || '';
const userAvatar = localStorage.getItem('avatar') || '';
const isCollapse = ref(false);

const goToPersonalCenter = () => {
  router.push('/home/shelter/personal-center');
};

const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('success');//
    localStorage.removeItem('email');//
    localStorage.removeItem('phone');//
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    localStorage.removeItem('avatar');
    localStorage.removeItem('shelterId');
    
    ElMessage.success('已退出登录');
    router.push('/');
  }).catch(() => {});
};
</script>

<style scoped>
.shelter-home {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 16px 24px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-section h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 4px 0 0;
  color: #909399;
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #606266;
}

.main-content {
  display: flex;
  margin-top: 80px;
  min-height: calc(100vh - 80px);
}

.shelter-menu {
  width: 220px;
  /* max-height: 1000px; */
  /* height: 100%; */
  border-right: none;
  background-color: #fff;
  box-shadow: 2px 0 8px 0 rgba(0, 0, 0, 0.1);
  transition: width 0.3s;
}

.menu-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #ebeef5;
}

.logo {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.menu-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.content-area {
  flex: 1;
  padding: 24px;
  background-color: #f5f7fa;
  overflow-y: auto;
}

.el-menu-item {
  height: 60px;
  line-height: 50px;
  font-size: 15px;
}

.el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 19px;
}

.el-menu-item:hover {
  background-color: #f0f9ff;
}

.el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409eff;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
}

:deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 8px;
}
</style>