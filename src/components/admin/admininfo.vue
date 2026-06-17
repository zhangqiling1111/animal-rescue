<template>
    <el-card class="user-info-container">
      <template #header>
        <div class="card-header">
          <h3 class="title">管理员个人信息</h3>
          <div class="action-buttons">
            <el-button type="primary" @click="openEditDialog">编辑信息</el-button>
            <el-button type="success" @click="openPasswordDialog">修改密码</el-button>
          </div>
        </div>
      </template>
  
      <el-skeleton :rows="4" animated v-if="loading" />
      
      <div v-else class="info-content">
        <el-row class="info-item">
          <el-col :span="4" class="label">账号：</el-col>
          <el-col :span="20" class="value">{{ userInfo.account }}</el-col>
        </el-row>
        
        <el-row class="info-item">
          <el-col :span="4" class="label">邮箱：</el-col>
          <el-col :span="20" class="value">{{ userInfo.email }}</el-col>
        </el-row>
        
        <el-row class="info-item">
          <el-col :span="4" class="label">手机号：</el-col>
          <el-col :span="20" class="value">{{ userInfo.phone }}</el-col>
        </el-row>
        
        <el-row class="info-item">
          <el-col :span="4" class="label">头像：</el-col>
          <el-col :span="20" class="value">
            <el-avatar :size="100" :src="userInfo.avatar" />
          </el-col>
        </el-row>
      </div>    
    </el-card>
    <el-dialog
        class="edit-dialog"
        title="修改个人信息"
        v-model="editDialogVisible"
        width="500px"
        :close-on-click-modal="false"
        >
        <el-form 
            :model="form" 
            :rules="rules" 
            ref="formRef"
            label-position="top"
        >
            <!-- 账号展示 -->
            <el-form-item label="账 号" class="form-item-enhanced">
            <el-input 
                v-model="userInfo.account" 
                disabled
                class="disabled-input elegant-input"
                prefix-icon="el-icon-user"
            ></el-input>
            </el-form-item>

            <!-- 邮箱输入 -->
            <el-form-item label="电子邮箱" prop="email" class="form-item-enhanced">
            <el-input 
                v-model="form.email"
                placeholder="请输入新的邮箱地址"
                class="elegant-input"
                prefix-icon="el-icon-message"
            >
                <template #suffix>
                <el-icon class="input-icon-hover"><el-icon-edit/></el-icon>
                </template>
            </el-input>
            </el-form-item>

            <!-- 手机号输入 -->
            <el-form-item label="手机号码" prop="phone" class="form-item-enhanced">
            <el-input 
                v-model="form.phone"
                placeholder="请输入新的手机号码"
                class="elegant-input"
                prefix-icon="el-icon-mobile-phone"
            >
                <template #suffix>
                <el-icon class="input-icon-hover"><el-icon-edit/></el-icon>
                </template>
            </el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer-enhanced">
            <el-button 
                class="enhanced-cancel-btn"
                @click="editDialogVisible = false"
            >
                取消修改
            </el-button>
            <el-button 
                class="enhanced-confirm-btn"
                type="primary"
                @click="handleEdit"
            >保存更改</el-button>
            </div>
        </template>
    </el-dialog>
    <el-dialog
        class="edit-dialog"
        title="修改密码"
        v-model="editpasswordDialogVisible"
        width="500px"
        :close-on-click-modal="false"
        >
        <el-form 
            :model="pwdForm" 
            :rules="pwdRules" 
            ref="formRef"
            label-position="top"
        >           
            <!-- 原密码输入 -->
            <el-form-item label="原密码" prop="oldPassword" class="form-item-enhanced">
            <el-input 
                v-model="pwdForm.oldPassword"
                placeholder="请输入原密码"
                class="elegant-input"
                show-password
                clearable
            >
                <template #prefix>
                <el-icon class="input-icon-hover"><el-icon-edit/></el-icon>
                </template>
            </el-input>
            </el-form-item>

            <!-- 新密码输入 -->
            <el-form-item label="新密码" prop="newPassword" class="form-item-enhanced">
            <el-input 
                v-model="pwdForm.newPassword"
                placeholder="请输入新密码(至少8位字符)"
                class="elegant-input"
                show-password
                clearable
            >
                <template #prefix>
                <el-icon class="input-icon-hover"><el-icon-edit/></el-icon>
                </template>
            </el-input>
            </el-form-item>
            <!-- 确认密码输入 -->
            <el-form-item label="确认密码" prop="confirmPassword" class="form-item-enhanced">
                <el-input 
                    v-model="pwdForm.confirmPassword"
                    placeholder="确认密码"
                    class="elegant-input"
                    show-password
                    clearable
                >
                    <template #prefix>
                    <el-icon class="input-icon-hover"><el-icon-edit/></el-icon>
                    </template>
                </el-input>
                </el-form-item>
            </el-form>
        <template #footer>
            <div class="dialog-footer-enhanced">
            <el-button 
                class="enhanced-cancel-btn"
                @click="editpasswordDialogVisible = false"
            >
                取消修改
            </el-button>
            <el-button 
                class="enhanced-confirm-btn"
                type="primary"
                @click="handlePassword"
            >保存更改</el-button>
            </div>
        </template>
    </el-dialog>
</template>
  
<script setup lang="ts">
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  import service from '../../service/user.js'
  const loading = ref(true);
  const editDialogVisible = ref(false);
  const editpasswordDialogVisible = ref(false);
  
  // 密码表单数据
  const pwdForm = ref({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  const rules = {
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
    ]
  };
  // 密码验证规则
  const pwdRules = {
    oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
    ],
    // newPassword: [
    //     { required: true, message: '请输入新密码', trigger: 'blur' },
    //     { min: 8, message: '密码长度至少8位', trigger: 'blur' },
    //     { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+/, 
    //     message: '需包含大小写字母和数字' }
    // ],
    // confirmPassword: [
    //     { required: true, message: '请确认新密码', trigger: 'blur' },
    //     { validator: (rule, value, callback) => {
    //     if (value !== pwdForm.value.newPassword) {
    //         callback(new Error('两次输入密码不一致'))
    //     } else {
    //         callback()
    //     }
    //     }, trigger: 'blur' }
    // ]
  }
  const userInfo = ref({
    account: localStorage.getItem('username'),
    email: localStorage.getItem('email'),
    phone: localStorage.getItem('phone'),
    avatar: localStorage.getItem('avatar')
  });
  
  // 表单数据
  const form = ref({
    email: '',
    phone: ''
  })
  
  
  const openPasswordDialog = () => {
    editpasswordDialogVisible.value = true;
  };
  const handleEdit = async () => {
    try {
        // 调用修改接口，只传需要修改的字段      
        const response = await service.updateProfile(form.value)
        if ( response.code === 200) {
          ElMessage.success('修改成功');
          editDialogVisible.value = false;
          // 更新本地数据
          userInfo.value = { ...userInfo.value, ...form.value };
        }        
    } catch (error) {
        ElMessage.error('修改失败');
    }
  }
  //修改密码
  const handlePassword = async () => {
    try {
        // 调用修改接口，只传需要修改的字段      
        const response = await service.updatePassword(pwdForm.value)
        if ( response.code === 200) {
          ElMessage.success('修改成功');
          editpasswordDialogVisible.value = false;                
        }else{
          console.error('修改失败:', response.message) 
        }       
    } catch (error) {
        ElMessage.error('修改失败');
    }
  }
  // 打开对话框时初始化表单数据
    const openEditDialog = () => {
        form.value = {
            email: userInfo.value.email,
            phone: userInfo.value.phone
        }
        editDialogVisible.value = true;
    }
  
  onMounted(async () => {
    loading.value = false;
  });
</script>
  
<style scoped lang="scss">
  .user-info-container {
    
    max-width: 900px;
    max-height: 450px;
    margin: 30px auto;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 0;
  
      .title {
        color: #007bff;
        margin: 0;
        // color: #333;
        font-size: 20px;
      }
    }
  
    .info-content {
      padding: 20px;
  
      .info-item {
        margin-bottom: 20px;
        padding: 12px 0;
        border-bottom: 1px solid #eee;
  
        .label {
          color: #666;
          font-weight: 500;
          font-size: 14px;
        }
  
        .value {
          color: #333;
          font-size: 15px;
          display: flex;
          align-items: center;
        }
      }
    }
  
    .action-buttons {
      display: flex;
      gap: 10px;
    }
  }
  
  .el-avatar {
    border: 2px solid #eee;
    transition: transform 0.3s;
  
    &:hover {
      transform: scale(1.1);
    }
  }
  
  ::v-deep .edit-dialog{
    // 对话框整体样式
    // display: block;
    border-radius: 12px!important;
    background: linear-gradient(145deg, #f8fafe, #ffffff);
    
    .el-dialog__header {
        border-bottom: 1px solid #e4e7ed;
        .el-dialog__title {
        font-size: 20px;
        color: #2c3e50;
        font-weight: 600;
        letter-spacing: 0.5px;
        }
    }

    .form-item-enhanced {
        margin-bottom: 24px;
        
        .el-form-item__label {
        font-size: 14px;
        color: #5a6c7d;
        padding-bottom: 8px;
        font-weight: 500;
        }
    }

    .elegant-input {
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        
        .el-input__inner {
            height: 44px;
            border-radius: 8px;
            border: 1px solid #dcdfe6;
            padding-left: 40px;
            font-size: 14px;
            
            &:hover {
                border-color: #409eff;
                box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
            }
            
            &:focus {
                border-color: #409eff;
                box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
            }
        }

        .el-input__prefix {
            left: 12px;
            .el-icon {
                font-size: 18px;
                color: #909399;
            }
        }

        .input-icon-hover {
            cursor: pointer;
            padding: 0 8px;
            transition: color 0.3s;
            &:hover {
                color: #409eff;
            }
        }
    }

    .disabled-input {
        .el-input__inner {
        background-color: #f8fafc;
        color: #7c8ca5;
        border-color: #e4e7ed;
        }
    }

    .dialog-footer-enhanced {
        display: flex;
        justify-content: flex-end;
        gap: 16px;
        padding-top: 20px;
        border-top: 1px solid #e4e7ed;

        .enhanced-cancel-btn {
            border-radius: 6px;
            padding: 10px 24px;
            transition: all 0.3s;
            border: 1px solid #e4e7ed;
            
            &:hover {
                background: #f5f7fa;
                color: #409eff;
                border-color: #a0cfff;
            }
        }

        .enhanced-confirm-btn {
            
            border-radius: 6px;
            padding: 10px 24px;
            background: linear-gradient(135deg, #409eff, #3375b9);
            border: none;
            transition: all 0.3s;
            
            &:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
            }
        }
    }
  }
  
</style>