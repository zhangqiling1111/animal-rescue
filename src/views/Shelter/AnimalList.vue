<template>
  <div>
    <h2>救助站动物信息</h2>

    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchParams" class="search-form" style="margin-bottom: 20px;">
      <el-form-item label="品种">
        <el-input v-model="searchParams.breed" placeholder="输入品种"></el-input>
      </el-form-item>
      <el-form-item label="健康状况">
        <el-select v-model="searchParams.healthStatus" placeholder="选择健康状况" clearable>
          <el-option label="健康" value="健康"></el-option>
          <el-option label="受伤" value="受伤"></el-option>
          <el-option label="康复中" value="康复中"></el-option>
          <el-option label="需要治疗" value="需要治疗"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="是否可领养">
        <el-select v-model="searchParams.isAdoptable" placeholder="选择" clearable>
          <el-option label="是" :value="1"></el-option>
          <el-option label="否" :value="0"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="paginatedAnimals" style="width: 100%; margin-bottom: 20px;">
      <el-table-column label="照片" width="120">
        <template #default="{ row }">
          <el-image 
            :src="row.animalPhoto || 'default_animal.jpg'" 
            :preview-src-list="[row.animalPhoto || 'default_animal.jpg']"
            fit="cover"
            class="animal-image"
          />
        </template>
      </el-table-column>
      <el-table-column prop="breed" label="品种" />
      <el-table-column prop="age" label="年龄（月）" />
      <el-table-column prop="healthStatus" label="健康状况">
        <template #default="{ row }">
          <el-tag :type="getHealthStatusType(row.healthStatus)">
            {{ row.healthStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isAdoptable" label="是否可领养">
        <template #default="{ row }">
          <el-tag :type="row.isAdoptable ? 'success' : 'info'">
            {{ row.isAdoptable ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="领养状态">
        <template #default="{ row }">
          <el-tag :type="row.isAdoptable ? 'success' : 'info'">
            {{ row.isAdoptable ? '未领养' : '已领养' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">修改</el-button>
          <el-button type="success" size="small" @click="openUploadDialog(row)">上传照片</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[5, 10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 修改动物信息的对话框 -->
    <el-dialog v-model="dialogVisible" title="修改动物信息" width="30%">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="品种">
          <el-input v-model="editForm.breed" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="editForm.age" :min="0" />
        </el-form-item>
        <el-form-item label="健康状况">
          <el-input v-model="editForm.healthStatus" placeholder="请输入健康状况" />
        </el-form-item>
        <el-form-item label="是否可领养">
          <el-switch v-model="editForm.isAdoptable" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 上传照片对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传动物照片" width="400px">
      <div v-if="currentUploadAnimal">
        <p>为动物 <strong>ID: {{ currentUploadAnimal.animalId }}</strong> (品种: {{ currentUploadAnimal.breed }}) 上传照片</p>
        <el-upload
          ref="uploadRef"
          class="avatar-uploader"
          action=""
          :show-file-list="true"
          :limit="1"
          :auto-upload="false"
          :on-change="handleUploadChange"
          :on-exceed="handleUploadExceed"
          :accept="'image/jpeg, image/png'"
        >
          <template #trigger>
            <el-button type="primary">选择文件</el-button>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              仅限 jpg/png 格式。
            </div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :disabled="!selectedUploadFile" :loading="uploadLoading">上传</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import shelterService from '@/service/shelter.js';

const allAnimals = ref([]);
const dialogVisible = ref(false);
const uploadDialogVisible = ref(false);
const editForm = ref({
  id: '',
  breed: '',
  age: 0,
  healthStatus: '',
  isAdoptable: false,
});
const currentUploadAnimal = ref(null);
const selectedUploadFile = ref(null);
const uploadLoading = ref(false);
const uploadRef = ref();

// 分页状态
const currentPage = ref(1);
const pageSize = ref(10); // 默认每页显示10条
const total = ref(0);

// 搜索参数
const searchParams = reactive({
  breed: '',
  area: '',
  healthStatus: '',
  isAdoptable: null,
  shelterName: ''
});

// Filtered animals based on searchParams (Client-side filtering)
const filteredAnimals = computed(() => {
  let result = allAnimals.value;

  // Filter by breed (case-insensitive substring)
  if (searchParams.breed) {
    const breedLower = searchParams.breed.toLowerCase();
    result = result.filter(animal => animal.breed && animal.breed.toLowerCase().includes(breedLower));
  }

  // Filter by healthStatus (exact match)
  if (searchParams.healthStatus) {
    result = result.filter(animal => animal.healthStatus === searchParams.healthStatus);
  }

  // Filter by isAdoptable (exact match, handle 1/0 or true/false)
  if (searchParams.isAdoptable !== null && searchParams.isAdoptable !== '') {
      const adoptableValue = typeof searchParams.isAdoptable === 'boolean' ? searchParams.isAdoptable : parseInt(searchParams.isAdoptable, 10) === 1;
      result = result.filter(animal => {
          // Normalize animal.isAdoptable to boolean if it's 0 or 1
          const animalAdoptable = typeof animal.isAdoptable === 'boolean' ? animal.isAdoptable : parseInt(animal.isAdoptable, 10) === 1;
          return animalAdoptable === adoptableValue;
      });
  }
  
  // Filter by area (case-insensitive substring) - Assuming area is a field in animal data
  if (searchParams.area) {
      const areaLower = searchParams.area.toLowerCase();
      // Make sure animal.area exists before filtering
      result = result.filter(animal => animal.area && animal.area.toLowerCase().includes(areaLower)); 
  }

  return result;
});

// Watch filteredAnimals to update total count and reset pagination
watch(filteredAnimals, (newFilteredList) => {
  total.value = newFilteredList.length;
  // Reset to first page when filters change
  if (currentPage.value !== 1) {
      currentPage.value = 1;
  }
});

// 计算当前页显示的动物数据 (前端分页 based on filteredAnimals)
const paginatedAnimals = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  // Slice the filtered list for pagination
  return filteredAnimals.value.slice(start, end);
});

// 健康状态样式映射
const getHealthStatusType = (status) => {
  const map = {
    '健康': 'success',
    '康复中': 'warning',
    '需要治疗': 'danger'
  };
  return map[status] || 'info';
};

// 获取动物列表
const fetchAnimals = async () => {
  try {
    const shelterId = localStorage.getItem('shelterId');
    if (!shelterId) {
      ElMessage.error('无法获取救助站ID，请重新登录');
      allAnimals.value = [];
      return;
    }
    console.log(`调用 getAnimalsInShelter 获取救助站 ${shelterId} 的动物列表`);
    const response = await shelterService.getAnimalsInShelter(shelterId);
    console.log('获取到的动物数据原始响应:', response);

    let animalData = [];
    if (response && response.data) {
      if (Array.isArray(response.data)) {
        animalData = response.data;
      } else if (response.data.records && Array.isArray(response.data.records)) {
        animalData = response.data.records;
      } else if (response.data.data && Array.isArray(response.data.data)) {
        animalData = response.data.data;
      }
    }
    allAnimals.value = animalData; // Store in allAnimals
    console.log('动物列表获取成功，总数 (原始):', allAnimals.value.length);
  } catch (error) {
    console.error('获取动物列表失败:', error);
    ElMessage.error(`获取动物列表失败: ${error.message}`);
    allAnimals.value = [];
  }
};

// 处理搜索 - Now does nothing as filtering is reactive
const handleSearch = () => {
  console.log('客户端筛选已根据 searchParams 自动更新');
};

// 重置搜索
const resetSearch = () => {
  searchParams.breed = '';
  searchParams.area = '';
  searchParams.healthStatus = '';
  searchParams.isAdoptable = null;
  searchParams.shelterName = '';
  console.log('搜索条件已重置，列表将显示所有动物');
};

// 处理修改
const handleEdit = (row) => {
  console.log('编辑行数据完整信息:', JSON.stringify(row, null, 2));
  editForm.value = {
    id: row.animalId,
    breed: row.breed,
    age: row.age,
    healthStatus: row.healthStatus || '健康',
    isAdoptable: row.isAdoptable == 1,
  };
  console.log('编辑表单数据:', editForm.value);
  dialogVisible.value = true;
};

// 提交修改
const submitEdit = async () => {
  try {
    if (!editForm.value.id) {
      ElMessage.error('无法获取动物ID，请检查数据格式');
      return;
    }
    console.log('提交修改，动物ID:', editForm.value.id);
    const data = {
      animalId: editForm.value.id,
      breed: editForm.value.breed,
      age: editForm.value.age,
      healthStatus: editForm.value.healthStatus,
      isAdoptable: editForm.value.isAdoptable ? 1 : 0
    };
    console.log('提交的数据:', data);
    const response = await shelterService.updateAnimal(editForm.value.id, data);
    console.log('更新响应:', response);
    
    // Instead of fetching, update the local array directly
    const index = allAnimals.value.findIndex(animal => animal.animalId === editForm.value.id);
    if (index !== -1) {
        // Update the properties of the found animal
        // Ensure the updated data matches the structure in allAnimals
        allAnimals.value[index] = {
            ...allAnimals.value[index], // Keep existing properties (like photo)
            breed: data.breed,
            age: data.age,
            healthStatus: data.healthStatus,
            isAdoptable: data.isAdoptable // Store as 0 or 1, consistent with backend
        };
        console.log('本地 allAnimals 数组已更新:', allAnimals.value[index]);
    } else {
        console.warn('未能找到要更新的本地动物数据，将重新获取列表以确保同步');
        await fetchAnimals(); // Fallback to fetching if local update fails
    }
    
    ElMessage.success('修改成功');
    dialogVisible.value = false;
  } catch (error) {
    console.error('修改失败:', error);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误信息:', error.response.data);
      ElMessage.error('修改失败: ' + (error.response.data?.message || error.message));
    } else {
      ElMessage.error('修改失败: ' + error.message);
    }
  }
};

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该动物信息吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      if (!row.animalId) {
        ElMessage.error('无法获取动物ID');
        return;
      }
      console.log('删除动物数据:', row);
      console.log('删除动物ID:', row.animalId);
      
      const response = await shelterService.deleteAnimal(row.animalId);
      console.log('删除响应:', response);
      
      // 如果请求成功完成（没有抛出异常），就认为删除成功
      ElMessage.success('删除成功');
      // 从本地列表中移除该动物
      allAnimals.value = allAnimals.value.filter(animal => animal.animalId !== row.animalId);
      // 重新获取动物列表以确保数据同步
      await fetchAnimals();
    } catch (error) {
      console.error('删除失败:', error);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误信息:', error.response.data);
        ElMessage.error('删除失败: ' + (error.response.data?.message || error.message));
      } else {
        ElMessage.error('删除失败: ' + error.message);
      }
    }
  }).catch(() => {});
};

// 打开上传对话框
const openUploadDialog = (row) => {
  currentUploadAnimal.value = row;
  selectedUploadFile.value = null; // Clear previous selection
  // Clear el-upload file list if needed
  if (uploadRef.value) {
      uploadRef.value.clearFiles();
  }
  uploadDialogVisible.value = true;
};

// 处理文件选择变化 (用于独立上传对话框)
const handleUploadChange = (uploadFile) => {
  const isJpgOrPng = uploadFile.raw.type === 'image/jpeg' || uploadFile.raw.type === 'image/png';

  if (!isJpgOrPng) {
      ElMessage.error('只能上传 JPG/PNG 格式!');
      selectedUploadFile.value = null;
      if (uploadRef.value) uploadRef.value.clearFiles();
      return;
  }
  
  selectedUploadFile.value = uploadFile.raw;
  console.log('Selected file:', selectedUploadFile.value);
};

// 处理超出限制
const handleUploadExceed = (files) => {
  if (uploadRef.value) {
      uploadRef.value.clearFiles(); // 清除已有文件
      const file = files[0]; // 获取新文件
      uploadRef.value.handleStart(file); // 手动添加新文件
      selectedUploadFile.value = file; // 更新选中文件引用
  }
  ElMessage.warning('只能选择一个文件，已替换为最新选择的文件');
};

// 提交上传
const submitUpload = async () => {
  if (!selectedUploadFile.value) {
    ElMessage.error('请先选择要上传的文件');
    return;
  }
  if (!currentUploadAnimal.value || !currentUploadAnimal.value.animalId) {
      ElMessage.error('无法获取动物ID');
      return;
  }

  uploadLoading.value = true;
  try {
    const animalId = currentUploadAnimal.value.animalId;
    const file = selectedUploadFile.value;

    console.log(`准备上传照片: animalId=${animalId}, 文件=${file.name}`);
    
    // Directly call the service function
    const response = await shelterService.uploadAnimalPhoto(animalId, file);
    console.log('上传照片响应:', response);

    if (response.code === 200) {
      ElMessage.success('照片上传成功');
      uploadDialogVisible.value = false;
      await fetchAnimals();
    } else {
      ElMessage.error(response.message || '上传失败');
    }
  } catch (error) {
    console.error('上传失败:', error);
    let errorMsg = '上传失败';
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || error.response.data.msg || error.message || '未知错误';
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error(errorMsg);
  } finally {
    uploadLoading.value = false;
  }
};

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1; // 切换每页数量时，回到第一页
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

onMounted(() => {
  fetchAnimals();
});
</script>

<style scoped>
.search-form .el-form-item {
  margin-right: 15px; /* 增加搜索项间距 */
}

.animal-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px; /* 轻微圆角 */
}

/* 可以根据需要添加更多样式 */
.el-table {
  margin-top: 15px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end; /* 分页组件右对齐 */
}

.dialog-footer {
  text-align: right;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px; /* 根据需要调整 */
  height: 178px; /* 根据需要调整 */
  text-align: center;
}

.avatar-uploader .el-upload-list__item {
  width: 178px; /* 预览图大小 */
  height: 178px;
}
</style>