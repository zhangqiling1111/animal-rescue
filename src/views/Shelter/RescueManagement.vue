<template>
  <div class="rescue-management">
    <div class="page-header">
      <h2>救助管理</h2>
      <p class="subtitle">管理用户提交的救助上报</p>
    </div>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>救助上报列表</span>
          <el-button type="primary" @click="refreshList">
            <el-icon><Refresh /></el-icon>刷新
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="rescues"
        style="width: 100%"
        border
      >
        <el-table-column prop="reportId" label="救助ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportedAt" label="上报时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.reportedAt || row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="150" show-overflow-tooltip />
        <el-table-column prop="healthStatus" label="健康状况" width="120" show-overflow-tooltip />
        <el-table-column fixed="right" label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewDetails(row)"
            >
              查看详情
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING' || row.status === '待处理'"
              type="success" 
              size="small" 
              @click="viewDetails(row)"
            >
              接收
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING' || row.status === '待处理'"
              type="danger" 
              size="small" 
              @click="viewDetails(row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
          layout="prev, pager, next"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="救助详情"
      width="60%"
    >
      <div v-if="currentRescue" class="detail-container">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="救助ID">
            {{ currentRescue.reportId }}
          </el-descriptions-item>
          <el-descriptions-item label="用户ID">
            {{ currentRescue.userId }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRescue.status)">
              {{ formatStatus(currentRescue.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="位置">
            {{ currentRescue.location || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="上报时间">
            {{ formatDate(currentRescue.reportedAt || currentRescue.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="健康状况">
            {{ currentRescue.healthStatus || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="描述信息">
            {{ currentRescue.description || currentRescue.desc || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="图片">
            <div v-if="currentRescue.photos || currentRescue.photo" class="photo-container">
              <el-image
                :src="currentRescue.photos || currentRescue.photo"
                :preview-src-list="[currentRescue.photos || currentRescue.photo]"
                fit="cover"
                style="width: 200px; height: 200px"
              />
            </div>
            <span v-else>无</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentRescue.checkReason || currentRescue.reviewReason" label="审核理由">
            {{ currentRescue.checkReason || currentRescue.reviewReason }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentRescue.checkedAt || currentRescue.reviewedAt" label="审核时间">
            {{ formatDate(currentRescue.checkedAt || currentRescue.reviewedAt) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 审核表单 (仅PENDING状态显示) -->
        <div v-if="currentRescue && (currentRescue.status === 'PENDING' || currentRescue.status === '待处理')" class="review-form-container">
          <el-divider>
            <el-icon><DocumentChecked /></el-icon>
            <span class="divider-content">审核操作</span>
          </el-divider>
          <el-form :model="detailReviewForm" label-width="100px" class="review-form">
            <el-form-item label="审核理由" required>
              <el-input 
                v-model="detailReviewForm.reason" 
                type="textarea" 
                :rows="4" 
                placeholder="请填写审核理由..."
              ></el-input>
            </el-form-item>
            <el-form-item>
              <div class="review-buttons">
                <el-button 
                  type="success" 
                  @click="handleDetailReview('ACCEPTED')"
                  :loading="detailSubmitLoading"
                >
                  <el-icon><Check /></el-icon>接收
                </el-button>
                <el-button 
                  type="danger" 
                  @click="handleDetailReview('REJECTED')"
                  :loading="detailSubmitLoading"
                >
                  <el-icon><Close /></el-icon>拒绝
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>

        <!-- 创建救助记录按钮 (仅ACCEPTED状态显示) -->
        <div v-if="currentRescue && (currentRescue.status === 'ACCEPTED' || currentRescue.status === '已接收')" class="create-record-container">
          <el-button 
            type="success" 
            @click="openCreateRecordDialog"
          >
            <el-icon><DocumentAdd /></el-icon> 创建救助记录
          </el-button>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 创建救助记录对话框 -->
    <el-dialog
      v-model="createRecordDialogVisible"
      title="创建救助记录"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form :model="createRecordForm" label-width="100px">
        <el-form-item label="动物ID" required>
          <el-input 
            v-model="createRecordForm.animalId" 
            placeholder="请输入关联的动物ID"
            type="number"
          ></el-input>
        </el-form-item>
        <el-form-item label="救助详情" required>
          <el-input 
            v-model="createRecordForm.details" 
            type="textarea" 
            :rows="4" 
            placeholder="请填写救助详情，例如发现地点、时间、动物状况等"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createRecordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCreateRecord" :loading="createRecordLoading">
            提交记录
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Refresh, Check, Close, DocumentChecked, DocumentAdd } from '@element-plus/icons-vue';
import shelterService from '@/service/shelter.js';

const loading = ref(false);
const rescues = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const currentRescue = ref(null);

// 详情页面的审核状态
const detailReviewForm = ref({
  reason: ''
});
const detailSubmitLoading = ref(false);

// 创建救助记录状态
const createRecordDialogVisible = ref(false);
const createRecordForm = ref({
  animalId: '',
  details: ''
});
const createRecordLoading = ref(false);

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'ACCEPTED': 'success',
    'REJECTED': 'danger',
    '待处理': 'warning',
    '已接收': 'success',
    '已拒绝': 'danger'
  };
  return typeMap[status] || 'info';
};

const getStatusText = (status) => {
  return status || '未知';
};

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString();
};

const formatStatus = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'ACCEPTED': '已接收',
    'REJECTED': '已拒绝',
    // 可能后端返回的是中文状态
    '待处理': '待处理',
    '已接收': '已接收',
    '已拒绝': '已拒绝'
  };
  return statusMap[status] || status;
};

const fetchRescues = async () => {
  try {
    loading.value = true;
    console.log('==== 开始获取救助记录 ====');
    console.log('当前页码:', currentPage.value);
    
    const token = localStorage.getItem('token');
    const shelterId = localStorage.getItem('shelterId');
    
    console.log('Token:', token ? `${token.substring(0, 15)}...` : '未找到');
    console.log('救助站ID:', shelterId || '未找到');
    
    // 验证必要参数
    if (!token) {
      console.error('获取救助记录失败: 未找到token');
      loading.value = false;
      rescues.value = [];
      total.value = 0;
      return;
    }
    
    if (!shelterId) {
      console.error('获取救助记录失败: 未找到救助站ID');
      loading.value = false;
      rescues.value = [];
      total.value = 0;
      return;
    }
    
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      timestamp: new Date().getTime() // 添加时间戳防止缓存
    };
    
    console.log('请求参数:', params);
    
    const response = await shelterService.getRescueReports(params);
    console.log('API响应完整数据:', response);
    
    // 提取救助记录数据
    let rescueArray = [];
    let totalCount = 0;
    
    if (response && response.data) {
      // 如果是直接返回的数据数组
      if (Array.isArray(response.data)) {
        console.log('API直接返回数组数据，长度:', response.data.length);
        rescueArray = response.data;
        totalCount = response.data.length;
      } 
      // 如果数据在data.records中
      else if (response.data.records && Array.isArray(response.data.records)) {
        console.log('数据在records字段中，长度:', response.data.records.length);
        rescueArray = response.data.records;
        totalCount = response.data.total || response.data.records.length;
      }
      // 如果数据在data.data中
      else if (response.data.data && Array.isArray(response.data.data)) {
        console.log('数据在data字段中，长度:', response.data.data.length);
        rescueArray = response.data.data;
        totalCount = response.data.total || response.data.data.length;
      }
      // 如果找不到数组，遍历寻找可能的数组字段
      else {
        console.log('尝试在返回数据中查找数组字段');
        for (const key in response.data) {
          if (Array.isArray(response.data[key])) {
            console.log(`找到数组字段 ${key}，长度:`, response.data[key].length);
            rescueArray = response.data[key];
            totalCount = response.data.total || rescueArray.length;
            break;
          }
        }
      }
    }
    
    console.log('解析后的救助记录数组:', rescueArray);
    console.log('解析后的总数:', totalCount);
    
    rescues.value = rescueArray;
    total.value = totalCount;
    
    console.log('==== 获取救助记录完成 ====');
  } catch (error) {
    console.error('获取救助记录失败:', error);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误数据:', error.response.data);
    }
    rescues.value = [];
    total.value = 0;
    
    // 显示错误消息
    ElMessage.error('获取救助记录失败，请稍后再试');
  } finally {
    loading.value = false;
  }
};

const viewDetails = async (row) => {
  try {
    console.log('查看救助详情:', row);
    
    // 直接使用行数据显示详情，不调用API
    currentRescue.value = row;
    
    // 重置审核表单
    detailReviewForm.value.reason = '';
    detailSubmitLoading.value = false;
    
    // 重置创建记录表单
    createRecordForm.value.animalId = '';
    createRecordForm.value.details = '';
    createRecordLoading.value = false;
    
    // 显示对话框
    dialogVisible.value = true;
  } catch (error) {
    console.error('处理详情失败:', error);
    ElMessage.error('处理详情失败');
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchRescues();
};

const refreshList = () => {
  currentPage.value = 1;
  fetchRescues();
};

const handleDetailReview = async (action) => {
  if (!detailReviewForm.value.reason.trim()) {
    ElMessage.warning('请填写审核理由');
    return;
  }

  try {
    detailSubmitLoading.value = true;
    
    console.log('提交审核数据:', {
      reportId: currentRescue.value.reportId,
      status: action,
      reason: detailReviewForm.value.reason
    });
    
    // 调用更新救助状态API
    const response = await shelterService.updateRescueStatus(
      currentRescue.value.reportId, 
      action,
      detailReviewForm.value.reason
    );
    
    console.log('审核API响应:', response);
    
    // 检查API响应
    if (response.code === 200 || response.status === 200) {
      // 显示成功消息
      ElMessage.success(
        action === 'ACCEPTED' 
          ? '已接受救助申请' 
          : '已拒绝救助申请'
      );
      
      // 关闭对话框
      dialogVisible.value = false;
      
      // 刷新数据列表
      fetchRescues();

      // 如果审核通过，可能需要更新当前记录状态以便显示"创建救助记录"按钮
      if (action === 'ACCEPTED' && (response.code === 200 || response.status === 200)) {
          // 如果API返回了更新后的数据，可以直接用
          if (response.data) {
             currentRescue.value = { ...currentRescue.value, ...response.data };
          } else {
             // 否则手动更新状态
             currentRescue.value.status = 'ACCEPTED'; 
          }
      }
    } else {
      // 显示错误消息
      const errorMsg = response.message || response.msg || '操作失败';
      ElMessage.error('审核失败: ' + errorMsg);
    }
  } catch (error) {
    console.error('审核失败:', error);
    let errorMsg = '未知错误';
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || error.response.data.msg || error.message || '操作失败';
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error('审核失败: ' + errorMsg);
  } finally {
    detailSubmitLoading.value = false;
  }
};

// 打开创建救助记录对话框
const openCreateRecordDialog = () => {
  // 可以尝试从当前救助信息中预填一些内容
  createRecordForm.value.details = `救助上报 #${currentRescue.value.reportId} 已处理。地点: ${currentRescue.value.location || '未知'}，健康状况: ${currentRescue.value.healthStatus || '未知'}。`;
  createRecordDialogVisible.value = true;
};

// 提交创建救助记录
const submitCreateRecord = async () => {
  // 验证表单
  if (!createRecordForm.value.animalId) {
    ElMessage.warning('请输入动物ID');
    return;
  }
  if (!createRecordForm.value.details.trim()) {
    ElMessage.warning('请填写救助详情');
    return;
  }

  try {
    createRecordLoading.value = true;
    const data = {
      animalId: createRecordForm.value.animalId,
      details: createRecordForm.value.details
    };

    console.log('尝试上传救助记录:', data);
    const response = await shelterService.uploadRescueRecord(data);
    console.log('上传救助记录响应:', response);

    if (response.code === 200) {
      ElMessage.success('救助记录上传成功');
      createRecordDialogVisible.value = false;
      // 可以在这里选择关闭详情对话框或保持打开
      // dialogVisible.value = false;
      // 刷新列表可能会改变当前行的状态，或者可以考虑直接更新当前行数据
      // fetchRescues(); 
    } else {
      ElMessage.error('上传失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('上传救助记录失败:', error);
    let errorMsg = '未知错误';
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || error.response.data.msg || error.message || '操作失败';
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error('上传失败: ' + errorMsg);
  } finally {
    createRecordLoading.value = false;
  }
};

onMounted(() => {
  fetchRescues();
});
</script>

<style scoped>
.rescue-management {
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

.list-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-button) {
  margin-left: 8px;
}

.detail-container {
  padding: 20px;
}

.photo-container {
  margin-top: 10px;
  margin-bottom: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.review-form-container {
  margin-top: 20px;
}

.review-form {
  padding: 10px;
}

.divider-content {
  font-weight: bold;
}

.review-buttons {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.create-record-container {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5; /* 添加分割线 */
  text-align: right; /* 按钮右对齐 */
}
</style> 