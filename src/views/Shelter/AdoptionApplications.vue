<template>
  <div class="adoption-applications">
    <div class="page-header">
      <h2>领养申请审核</h2>
      <p class="subtitle">管理用户的领养申请</p>
    </div>

    <!-- Filter Form - Simplified for Status only -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchParams">
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="按状态筛选 (客户端)" clearable @change="handleStatusFilterChange">
            <el-option label="审核中" value="审核中" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已拒绝" value="已拒绝" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetFiltersAndFetchAll">
            <el-icon><Refresh /></el-icon>显示全部/重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>申请列表 (点击行查看该用户所有记录)</span>
          <el-button type="primary" @click="refreshList">
            <el-icon><Refresh /></el-icon>刷新完整列表
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="paginatedApplications"
        style="width: 100%"
        border
        @row-click="handleRowClick"
        :row-style="{ cursor: 'pointer' }"
      >
        <el-table-column prop="applicationId" label="申请ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="animalId" label="动物ID" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appliedAt" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.appliedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="150">
           <template #default="{ row }">
             {{ row.notes || '-' }}
           </template>
        </el-table-column>
        <template v-if="!isShowingSingleUser">
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button 
                type="primary" 
                size="small" 
                @click.stop="viewDetails(row)"
              >
                查看详情
              </el-button>
              <el-button 
                v-if="row.status === '审核中'" 
                type="success" 
                size="small" 
                @click.stop="handleApprove(row)"
              >
                通过
              </el-button>
              <el-button 
                v-if="row.status === '审核中'" 
                type="danger" 
                size="small" 
                @click.stop="handleReject(row)"
              >
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </template>
      </el-table>

      <div v-if="!loading && paginatedApplications.length === 0" class="empty-data">
        <el-empty description="暂无符合条件的领养申请" />
      </div>

      <div v-else-if="!loading && total > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
          layout="total, prev, pager, next"
        />
      </div>
    </el-card>

    <!-- 恢复详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="申请详情"
      width="60%"
    >
      <div v-if="currentApplication" class="application-details">
        <el-descriptions title="申请信息" :column="2" border>
          <el-descriptions-item label="申请ID">
            {{ currentApplication.applicationId }}
          </el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusType(currentApplication.status)">
              {{ getStatusText(currentApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ formatDate(currentApplication.appliedAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注">
            {{ currentApplication.notes || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <div class="info-section">
          <div class="user-info">
            <h3>申请人信息</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户ID">
                {{ currentApplication.userId }}
              </el-descriptions-item>
              <el-descriptions-item label="用户名" v-if="currentApplication.userName">
                {{ currentApplication.userName }}
              </el-descriptions-item>
              <el-descriptions-item label="联系方式" v-if="currentApplication.userContact">
                {{ currentApplication.userContact }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="animal-info">
            <h3>动物信息</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="动物ID">
                {{ currentApplication.animalId }}
              </el-descriptions-item>
              <el-descriptions-item label="动物名称" v-if="currentApplication.animalName">
                {{ currentApplication.animalName }}
              </el-descriptions-item>
              <el-descriptions-item label="品种" v-if="currentApplication.animalBreed">
                {{ currentApplication.animalBreed }}
              </el-descriptions-item>
              <el-descriptions-item label="照片" v-if="currentApplication.animalPhoto">
                <el-image 
                  :src="currentApplication.animalPhoto"
                  :preview-src-list="[currentApplication.animalPhoto]"
                  fit="cover"
                  style="width: 150px; height: 150px"
                />
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>

        <el-divider />

        <!-- 恢复对话框页脚按钮 -->
        <div class="dialog-footer" v-if="currentApplication.status === '审核中'">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="danger" @click="handleRejectInDialog(currentApplication)">拒绝申请</el-button>
          <el-button type="primary" @click="handleApproveInDialog(currentApplication)">通过申请</el-button>
        </div>
         <div class="dialog-footer" v-else>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import shelterService from '@/service/shelter.js';

const loading = ref(false);
const allApplications = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const currentApplication = ref(null);
const isShowingSingleUser = ref(false);

// Search parameters - Only status is needed for client-side filtering now
const searchParams = reactive({
  status: ''
});

// Computed property for filtered applications (now only filters by status)
const filteredApplications = computed(() => {
  let result = allApplications.value;

  // Filter by status (exact match)
  if (searchParams.status) {
    result = result.filter(app => app.status === searchParams.status);
  }

  return result;
});

// Watch filteredApplications to update total and reset pagination
watch(filteredApplications, (newFilteredList) => {
  total.value = newFilteredList.length;
  if (currentPage.value !== 1 && newFilteredList.length > 0) {
    currentPage.value = 1;
  } else if (currentPage.value > 1 && newFilteredList.length === 0) {
     currentPage.value = 1;
  }
});

// Computed property for paginated applications
const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredApplications.value.slice(start, end);
});

const getStatusType = (status) => {
  const types = {
    '审核中': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  };
  return types[status] || 'info';
};

const getStatusText = (status) => {
  return status;
};

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString();
};

const fetchApplications = async () => {
  try {
    loading.value = true;
    console.log('==== 开始获取领养申请列表 (获取全部) ===');

    const token = localStorage.getItem('token');
    const shelterId = localStorage.getItem('shelterId');

    if (!token || !shelterId) {
      ElMessage.error('无法获取用户信息或救助站ID，请重新登录');
      loading.value = false;
      allApplications.value = [];
      return;
    }

    const params = { limit: 10000 }; // Fetch all
    console.log('请求参数 (获取全部):', params);
    const response = await shelterService.getAdoptionApplications(params);
    console.log('API 原始响应 (获取全部):', JSON.stringify(response, null, 2));

    // Simplified response handling
    let applicationArray = null;
    const responseData = response.data;
    if (responseData) {
      if (responseData.records && Array.isArray(responseData.records)) {
        applicationArray = responseData.records;
      } else if (responseData.data && Array.isArray(responseData.data)) {
        applicationArray = responseData.data;
      } else if (Array.isArray(responseData)) {
        applicationArray = responseData;
      } else {
         for (const key in responseData) {
            if (Array.isArray(responseData[key])) {
                applicationArray = responseData[key];
                break;
            }
         }
      }
    }
    if (!applicationArray) { applicationArray = []; }
    
    allApplications.value = applicationArray;
    searchParams.status = ''; // Clear status filter on full refresh
    if (currentPage.value !== 1) currentPage.value = 1; // Reset page on full refresh
    isShowingSingleUser.value = false; // Set state to show all users
    console.log('已加载完整列表，isShowingSingleUser 设置为 false');

  } catch (error) {
    console.error('获取所有领养申请列表失败:', error);
    if (error.response) { console.error('错误数据:', error.response.data); }
    allApplications.value = [];
    ElMessage({ message: '获取领养申请列表失败，请稍后再试', type: 'error', duration: 3000 });
  } finally {
    loading.value = false;
  }
};

// NEW: Handle row click to fetch and display only that user's records
const handleRowClick = async (row, column, event) => {
  // Check if the click was on the operations column
  if (column && column.label === '操作') {
    console.log('点击在操作列，忽略行点击事件。');
    return; // Do nothing if click is on the operations column
  }

  if (!row || !row.userId) return; // Basic check
  const userId = row.userId;
  console.log(`行点击: 查询用户 ${userId} 的记录`);
  loading.value = true;
  try {
    const response = await shelterService.getAdoptionRecordsByUserId(userId);
    console.log(`用户 ${userId} 记录 API 响应:`, JSON.stringify(response, null, 2));

    let userApplications = [];
    if (response && response.data) {
      if (Array.isArray(response.data)) {
        userApplications = response.data;
      } else if (response.data.data && Array.isArray(response.data.data)) {
        userApplications = response.data.data;
      }
    }
    
    allApplications.value = userApplications; // Overwrite list with user's data
    searchParams.status = ''; // Clear status filter
    if (currentPage.value !== 1) {
        currentPage.value = 1; // Go to page 1
    }
    isShowingSingleUser.value = true; // Set state to show single user
    ElMessage.info(`已显示用户 ${userId} 的所有领养记录`);
    console.log('已加载单个用户记录，isShowingSingleUser 设置为 true');

  } catch (error) {
    console.error(`查询用户 ${userId} 记录失败:`, error);
    const errorMsg = error.response?.data?.message || error.message || '查询失败';
    ElMessage.error(errorMsg);
    allApplications.value = []; // Clear list on error
  } finally {
    loading.value = false;
  }
};

// Handler for status filter change (client-side filtering)
const handleStatusFilterChange = () => {
    if (currentPage.value !== 1) {
        currentPage.value = 1;
    }
    // Computed properties handle the filtering
};

// Renamed resetSearch to resetFiltersAndFetchAll
// Clears status filter AND fetches the full list again
const resetFiltersAndFetchAll = () => {
  console.log('重置筛选并获取全部记录');
  searchParams.status = '';
  // No need to reset page here, fetchApplications does it
  fetchApplications(); 
};

// Refresh button function - now calls resetFiltersAndFetchAll
const refreshList = () => {
  resetFiltersAndFetchAll();
};

const viewDetails = (row) => {
  currentApplication.value = { ...row }; 
  dialogVisible.value = true;
};

const handleApprove = async (row) => {
  try {
    if (row.status !== '审核中') {
        ElMessage.warning('该申请当前状态不是"审核中"，无法批准。'); return;
    }
    const { value: notes } = await ElMessageBox.prompt(
      '请输入审核通过的备注（可选）',
      '通过申请',
      {
        confirmButtonText: '确定通过',
        cancelButtonText: '取消',
        inputPlaceholder: '例如：申请人条件符合，同意领养',
      }
    );
    const response = await shelterService.updateAdoptionStatus({ applicationId: row.applicationId, status: '已通过', notes: notes || '' });
    if (response && response.code === 200) {
      ElMessage.success('已通过申请');
      fetchApplications(); 
      if(dialogVisible.value) dialogVisible.value = false;
    } else { ElMessage.error(response?.message || '操作失败'); }
  } catch (error) { if (error !== 'cancel') { console.error('操作失败:', error); ElMessage.error('操作失败'); } }
};

const handleReject = async (row) => {
  try {
    if (row.status !== '审核中') { 
        ElMessage.warning('该申请当前状态不是"审核中"，无法拒绝。'); return; 
    }
    const { value: notes } = await ElMessageBox.prompt(
      '请输入审核拒绝的备注（必填）',
      '拒绝申请',
      {
        confirmButtonText: '确定拒绝',
        cancelButtonText: '取消',
        inputPlaceholder: '例如：住房条件不满足要求',
        inputValidator: (val) => {
          if (!val || val.trim().length === 0) {
            return '拒绝备注不能为空';
          }
          return true;
        },
        inputErrorMessage: '拒绝备注不能为空'
      }
    );
    const response = await shelterService.updateAdoptionStatus({ applicationId: row.applicationId, status: '已拒绝', notes: notes });
    if (response && response.code === 200) {
      ElMessage.success('已拒绝申请');
      fetchApplications(); 
      if(dialogVisible.value) dialogVisible.value = false;
    } else { ElMessage.error(response?.message || '操作失败'); }
  } catch (error) { if (error !== 'cancel') { console.error('操作失败:', error); ElMessage.error('操作失败'); } }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  // No fetch needed, pagination is client-side on filtered data
};

const handleApproveInDialog = (application) => { handleApprove(application); };
const handleRejectInDialog = (application) => { handleReject(application); };

onMounted(() => {
  fetchApplications();
});
</script>

<style scoped>
.adoption-applications { padding: 24px; }

.filter-card { margin-bottom: 20px; padding: 10px 20px 0; }
.filter-card .el-form-item { margin-bottom: 10px; }

.page-header { margin-bottom: 24px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; color: #303133; }
.subtitle { margin: 0; color: #909399; font-size: 14px; }

.list-card { margin-bottom: 24px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }

.empty-data { padding: 40px 0; }
.pagination-container { margin-top: 20px; text-align: right; }

.application-details .info-section { display: flex; gap: 20px; margin: 20px 0; }
.application-details .user-info, .application-details .animal-info { flex: 1; }
.application-details h3 { margin-top: 0; margin-bottom: 16px; color: #303133; }
.dialog-footer { margin-top: 20px; text-align: right; }

/* Add pointer cursor style to table rows */
:deep(.el-table__row) {
  cursor: pointer;
}
</style> 