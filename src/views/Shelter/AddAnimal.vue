<template>
  <div class="add-animal">
    <div class="page-header">
      <h2>添加动物信息</h2>
      <p class="subtitle">为救助站添加新的动物信息</p>
    </div>

    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>

      <el-form 
        :model="form" 
        :rules="rules"
        ref="formRef"
        label-width="120px"
        class="animal-form"
      >
        <el-form-item label="品种" prop="breed">
          <el-input 
            v-model="form.breed" 
            placeholder="请输入动物品种"
            clearable
          />
        </el-form-item>

        <el-form-item label="年龄（月）" prop="age">
          <el-input-number 
            v-model="form.age" 
            :min="0"
            :max="240"
            placeholder="请输入年龄"
          />
        </el-form-item>

        <el-form-item label="健康状况" prop="healthStatus">
          <el-input 
            v-model="form.healthStatus" 
            placeholder="请输入健康状况"
            clearable
          />
        </el-form-item>

        <el-form-item label="是否可领养" prop="isAdoptable">
          <el-switch 
            v-model="form.isAdoptable" 
            active-text="可领养"
            inactive-text="不可领养"
          />
        </el-form-item>

        <el-form-item label="照片" prop="selectedFile">
          <el-upload
            class="avatar-uploader"
            action=""
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleFileChange"
            accept="image/jpeg, image/png"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">
            <el-icon><Check /></el-icon>提交
          </el-button>
          <el-button @click="resetForm">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, Check, Refresh } from '@element-plus/icons-vue';
import shelterService from '@/service/shelter.js';
import { useRouter } from 'vue-router';

const formRef = ref(null);
const form = ref({
  breed: '',
  age: 0,
  healthStatus: '健康',
  isAdoptable: true,
});
const selectedFile = ref(null);
const imageUrl = ref('');

const rules = {
  breed: [
    { required: true, message: '请输入动物品种', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', message: '年龄必须为数字', trigger: 'blur' }
  ],
  healthStatus: [
    { required: true, message: '请输入健康状况', trigger: 'blur' }
  ],
};

const router = useRouter();

const handleFileChange = (uploadFile) => {
    console.log('File selected:', uploadFile);
    const isJPG = uploadFile.raw.type === 'image/jpeg';
    const isPNG = uploadFile.raw.type === 'image/png';

    if (!isJPG && !isPNG) {
        ElMessage.error('上传图片只能是 JPG/PNG 格式!');
        selectedFile.value = null;
        imageUrl.value = '';
        return false;
    }
    
    selectedFile.value = uploadFile.raw;
    imageUrl.value = URL.createObjectURL(uploadFile.raw);
};

const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();

    // 1. Prepare and submit the animal data (without photo)
    const animalData = {
      breed: form.value.breed,
      age: Number(form.value.age),
      healthStatus: form.value.healthStatus,
      isAdoptable: form.value.isAdoptable
    };

    console.log('Step 1: Submitting animal data:', animalData);
    const addAnimalResponse = await shelterService.addAnimal(animalData);
    console.log('Step 1 Response (/animals/addAnimal):', addAnimalResponse);

    if (addAnimalResponse.code !== 200) {
      // If adding animal data failed, stop here
      ElMessage.error(addAnimalResponse.message || '添加动物基本信息失败');
      return; 
    }

    // Extract the new animalId from the response (assuming it's in response.data.animalId)
    // ** Adjust this based on the ACTUAL response structure of /animals/addAnimal **
    const newAnimalId = addAnimalResponse.data?.animalId;
    if (!newAnimalId) {
        console.error('未能从添加动物响应中获取 newAnimalId:', addAnimalResponse.data);
        ElMessage.warning('动物信息添加成功，但未能获取新动物ID，无法上传照片。');
        // Optionally navigate away or just show success for the first part
        router.push('/home/shelter/animals'); 
        return;
    }

    console.log(`Step 1 Success. New animalId: ${newAnimalId}`);

    // 2. If a photo was selected, upload it using the new animalId
    if (selectedFile.value) {
      console.log(`Step 2: Uploading photo for animalId: ${newAnimalId}`);
      try {
        const uploadResponse = await shelterService.uploadAnimalPhoto(newAnimalId, selectedFile.value);
        console.log('Step 2 Response (/animals/uploadAnimalPhoto):', uploadResponse);
        
        if (uploadResponse.code === 200) {
          ElMessage.success('动物信息和照片均已成功添加!');
        } else {
          // Add animal was ok, but upload failed
          ElMessage.warning(`动物信息添加成功 (ID: ${newAnimalId})，但照片上传失败: ${uploadResponse.message || '未知错误'}`);
        }
      } catch (uploadError) {
        console.error('照片上传失败:', uploadError);
         let errorMsg = '照片上传失败';
         if (uploadError.response && uploadError.response.data) {
            errorMsg = uploadError.response.data.message || uploadError.response.data.msg || uploadError.message || '未知错误';
         } else if (uploadError.message) {
            errorMsg = uploadError.message;
         }
        ElMessage.warning(`动物信息添加成功 (ID: ${newAnimalId})，但照片上传失败: ${errorMsg}`);
      }
    } else {
      // No photo selected, just show success for adding animal data
      ElMessage.success('动物信息添加成功!');
    }

    // Navigate after everything is done (or attempted)
    router.push('/home/shelter/animals');

  } catch (error) {
    // Handle initial form validation error or step 1 (addAnimal) error
    console.error('添加过程出错:', error);
    if (error && error.message && error.message.includes('Network Error')) {
        ElMessage.error('网络错误，请检查连接');
    } else if (error && Array.isArray(error)) { // el-form validation error
        ElMessage.error('请检查表单填写是否正确');
    } else { 
        // Error from shelterService.addAnimal or other unexpected errors
        ElMessage.error(error.message || '添加失败，请联系管理员');
    }
  }
};

const resetForm = () => {
  if (!formRef.value) return;
  formRef.value.resetFields();
  selectedFile.value = null;
  imageUrl.value = '';
};
</script>

<style scoped>
.add-animal {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
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

.form-card {
  max-width: 800px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.animal-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px 0;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
  background-color: #f5f7fa;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}

:deep(.el-input__inner) {
  border-radius: 4px;
}

:deep(.el-button) {
  border-radius: 4px;
}

:deep(.el-form-item:last-child) {
  margin-bottom: 0;
  text-align: center;
}
</style>