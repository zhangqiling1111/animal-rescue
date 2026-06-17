<template>
  <div class="personal-center">
    <div class="page-header">
      <h2>个人中心</h2>
      <p class="subtitle">管理您的救助站信息和账户安全</p>
    </div>
    
    <div class="content-wrapper">
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-button v-if="!isEditing" type="primary" @click="startEdit" size="small">
              <el-icon><Edit /></el-icon>编辑信息
            </el-button>
          </div>
        </template>
        
        <div class="profile-section">
          <div class="avatar-container">
            <el-avatar :size="120" :src="userInfo.avatar || 'default_avatar.jpg'" />
            <div class="avatar-upload">
              <el-upload
                class="avatar-uploader"
                action="/api/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
              >
                
              </el-upload>
            </div>
          </div>
          
          <div class="info-container">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="救助站ID">
                <el-tag type="info">{{ userInfo.shelterId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="用户ID">
                <el-tag type="info">{{ userInfo.userId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="救助站名称">
                <span v-if="!isEditing">{{ userInfo.name }}</span>
                <el-input v-else v-model="editForm.name" placeholder="请输入救助站名称" />
              </el-descriptions-item>
              <el-descriptions-item label="地址">
                <span v-if="!isEditing">{{ userInfo.address }}</span>
                <el-input v-else v-model="editForm.address" placeholder="请输入地址" />
              </el-descriptions-item>
              <el-descriptions-item label="联系方式">
                <span v-if="!isEditing">{{ userInfo.contactInfo }}</span>
                <el-input v-else v-model="editForm.contactInfo" placeholder="请输入联系方式" />
              </el-descriptions-item>
            </el-descriptions>
            
            <div class="edit-buttons" v-if="isEditing">
              <el-button type="success" @click="saveEdit">
                <el-icon><Check /></el-icon>保存
              </el-button>
              <el-button @click="cancelEdit">
                <el-icon><Close /></el-icon>取消
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="password-card">
        <template #header>
          <div class="card-header">
            <span>修改密码</span>
          </div>
        </template>
        
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input 
              v-model="passwordForm.oldPassword" 
              type="password" 
              show-password 
              placeholder="请输入原密码"
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword" 
              type="password" 
              show-password 
              placeholder="请输入新密码"
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword" 
              type="password" 
              show-password 
              placeholder="请再次输入新密码"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleChangePassword">
              <el-icon><Key /></el-icon>修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Edit, Check, Close, Key } from '@element-plus/icons-vue';
import shelterService from '@/service/shelter.js';

const userInfo = ref({
  shelterId: localStorage.getItem('shelterId'),
  userId: localStorage.getItem('userId'),
  name: localStorage.getItem('username'),
  address: localStorage.getItem('address'),
  contactInfo: localStorage.getItem('contactInfo'),
  avatar: localStorage.getItem('avatar')
});

const isEditing = ref(false);
const editForm = ref({
  name: '',
  address: '',
  contactInfo: ''
});

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const passwordFormRef = ref(null);

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

const startEdit = () => {
  editForm.value = {
    name: userInfo.value.name,
    address: userInfo.value.address,
    contactInfo: userInfo.value.contactInfo
  };
  isEditing.value = true;
};

const cancelEdit = () => {
  isEditing.value = false;
};

const saveEdit = async () => {
  try {
    // 检查 editForm.value.name 是否为空
    if (!editForm.value.name) {
      ElMessage.error('救助站名称不能为空');
      return;
    }

    const updateData = {
      shelterId: userInfo.value.shelterId,
      shelterName: editForm.value.name, // 将 name 映射为 shelterName
      address: editForm.value.address,
      contactInfo: editForm.value.contactInfo
    };

    await shelterService.updateShelterInfo(updateData);

    // 更新 userInfo
    userInfo.value = {
      ...userInfo.value,
      ...editForm.value
    };

    ElMessage.success('信息更新成功');
    isEditing.value = false;
  } catch (error) {
    console.error('更新信息失败:', error);
    ElMessage.error(error.response?.data?.message || '更新信息失败');
  }
};

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return;
  
  try {
    await passwordFormRef.value.validate();
    await shelterService.changePassword(
      passwordForm.value.oldPassword,
      passwordForm.value.newPassword
    );
    ElMessage.success('密码修改成功');
    // 清空表单
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  } catch (error) {
    console.error('修改密码失败:', error);
    ElMessage.error(error.response?.data?.message || '修改密码失败');
  }
};

const fetchUserInfo = async () => {
  try {
    const response = await shelterService.getShelterInfo();
    console.log('获取到的救助站信息:', response.data);
    if (response.data) {
      userInfo.value = {
        shelterId: response.data.shelterId,
        userId: response.data.userId,
        name: response.data.name,
        address: response.data.address,
        contactInfo: response.data.contactInfo,
        avatar: userInfo.value.avatar
      };
      console.log('更新后的userInfo:', userInfo.value);
    }
  } catch (error) {
    console.error('获取个人信息失败:', error);
    ElMessage.error(error.response?.data?.message || '获取个人信息失败');
  }
};

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    userInfo.value.avatar = response.data.url;
    ElMessage.success('头像上传成功');
  } else {
    ElMessage.error('头像上传失败');
  }
};

onMounted(() => {
  fetchUserInfo();
});
</script>

<style scoped>
.personal-center {
  padding: 24px;
  min-height: calc(100vh - 200px);
  background-color: #f5f7fa;
}

.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.info-card, .password-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-section {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.avatar-container {
  text-align: center;
  flex-shrink: 0;
}

.avatar-upload {
  margin-top: 12px;
}

.info-container {
  flex: 1;
}

:deep(.el-descriptions__label) {
  width: 120px;
  font-weight: bold;
  color: #606266;
}

:deep(.el-descriptions__content) {
  padding-left: 20px;
}

.edit-buttons {
  margin-top: 20px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.el-form {
  max-width: 500px;
  margin: 0 auto;
}

:deep(.el-input__inner) {
  border-radius: 4px;
}

:deep(.el-button) {
  border-radius: 4px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fafafa;
}
</style> 