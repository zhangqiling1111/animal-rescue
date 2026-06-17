<template>
  <div class="user-view">
    <nav class="navbar">
      <div class="navbar-left">
        <span class="navbar-brand">流浪动物救助平台</span> 
        <router-link to="/home/adoptioncenter" class="nav-item">
          <img class="nav-icon" src="../assets/领养.png" />领养中心
        </router-link>
        <router-link to="/home/rescuestation" class="nav-item">
          <img class="nav-icon" src="../assets/救助站.png" />救助站
        </router-link>
        <!-- <router-link to="/home/mynews" class="nav-item">
          <img class="nav-icon" src="../assets/新闻.png" />相关资讯
        </router-link> -->
        <router-link to="/home/PersonalCenter" class="nav-item">
          <img class="nav-icon" src="../assets/用户.png" />个人中心
        </router-link>
      </div>
      <div class="navbar-right">
        <el-dropdown trigger="hover">
          <div class="avatar-wrapper">
            <el-avatar 
              :size="40" 
              src="https://iconfont.alicdn.com/p/illus/preview_image/vKhn39joZS7d/b3bb28bd-1cf2-4869-8701-985093ebe0ff.png"
              class="user-avatar"
            />
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown">
              <el-dropdown-item @click="logout">                
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </nav>
    <div class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage,ElMessageBox } from 'element-plus'

const logout = () => {
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
    localStorage.removeItem('success');//
    localStorage.removeItem('email');//
    localStorage.removeItem('phone');//
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    localStorage.removeItem('avatar');
    ElMessage.success('已退出登录');
    window.location.href = '/login'
  }).catch(() => {});
}
</script>

<style scoped lang="scss">
.user-view {
  display: flex;
  flex-direction: column;
  max-width: 100%;
  min-height: 100vh;
  background: #f5f7fa;
}

.nav-item {
  display: flex !important;
  align-items: center;
  gap: 8px; /* 图标和文字间距 */
  padding: 8px 12px !important;
  transition: all 0.3s;

  .nav-icon {
    width: 20px;
    height: 20px;
    filter: invert(90%) sepia(10%) saturate(100%) hue-rotate(180deg); /* 默认白色图标 */
  }

  &:hover .nav-icon {
    filter: invert(100%) sepia(100%) saturate(0%) hue-rotate(0deg); /* 悬停时保持白色 */
  }

  &.router-link-exact-active .nav-icon {
    filter: invert(100%) sepia(100%) saturate(100%) hue-rotate(200deg); /* 激活状态蓝色图标 */
  }
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2%;
  height: 64px;
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1000;
  position: fixed; /* 新增属性，使导航栏固定 */
  top: 0; /* 新增属性，将导航栏固定在顶部 */
  left: 0; /* 新增属性，确保导航栏从左侧开始 */
  width: 96%; /* 新增属性，使导航栏宽度铺满屏幕 */
  &-left {
    display: flex;
    align-items: center;
    gap: 40px;
  }

  &-brand {
    font-size: 24px;
    font-weight: 700;
    color: #fff;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
    letter-spacing: 1px;
    margin-right: 160px;
  }

  a {
    color: rgba(255, 255, 255, 0.9);
    text-decoration: none;
    padding: 8px 12px;
    border-radius: 4px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    font-size: 16px;

    &::after {
      content: '';
      position: absolute;
      bottom: -5px;
      left: 50%;
      width: 0;
      height: 2px;
      background: #fff;
      transition: all 0.3s;
    }

    &:hover {
      background: rgba(255, 255, 255, 0.1);
      
      &::after {
        width: 100%;
        left: 0;
      }
    }

    &.router-link-exact-active {
      color: #fff;
      font-weight: 500;
      
      &::after {
        width: 100%;
        left: 0;
      }
    }
  }
}

.avatar-wrapper {
  cursor: pointer;
  transition: transform 0.3s;
  outline: none !important;
  
  &:hover {
    transform: scale(1.1);
    
    .user-avatar {
      box-shadow: 0 0 12px rgba(255, 255, 255, 0.3);
    }
  }
}

.user-avatar {
  transition: all 0.3s;
  border: 2px solid rgba(255, 255, 255, 0.3);
  // margin-right: 30px;
}

.user-dropdown {
  // margin-top: 10px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  
  .el-dropdown-menu__item {
    padding: 12px 20px;
    color: #1a73e8;
    
    &:hover {
      background: #e8f4ff;
    }
    
  }
}
:deep(.el-dialog) {
    border-radius: 12px;
  }
.content {
  margin-top: 64px; /* 新增属性，避免内容被导航栏遮挡 */
  // background: #ffffff;
  background: linear-gradient(180deg, #e5f3fe, #ffffff);
}
</style>