<template>
  <div class="rescue-records">
    <div class="page-header">
      <h2>救助记录</h2>
      <p class="subtitle">查看救助站的所有救助记录</p>
    </div>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>救助记录列表</span>
          <div>
            <el-button type="primary" @click="handleSearch" :icon="Search" style="margin-left: 10px;">搜索</el-button>
            <el-button @click="handleReset" :icon="Refresh">重置</el-button>
          </div>
        </div>
      </template>

      <div class="filter-container">
        <el-form :inline="true" :model="filterData" class="filter-form">
          <el-form-item label="记录 ID">
            <el-input v-model="filterData.recordId" placeholder="输入记录 ID" clearable />
          </el-form-item>
          <el-form-item label="救助站 ID">
            <el-input v-model="filterData.shelterId" placeholder="输入救助站 ID" clearable />
          </el-form-item>
          <el-form-item label="动物 ID">
            <el-input v-model="filterData.animalId" placeholder="输入动物 ID" clearable />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="allRecords.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无救助记录" />
      </div>
      
      <el-table 
        v-else 
        :data="paginatedRecords" 
        style="width: 100%"
        :stripe="true"
        :border="true"
        v-loading="loading"
      >
        <el-table-column prop="recordId" label="记录 ID" width="120" />
        <el-table-column prop="shelterId" label="救助站 ID" width="120" />
        <el-table-column prop="animalId" label="动物 ID" width="120" />
        <el-table-column prop="rescueTime" label="救助时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.rescueTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="details" label="救助详情">
          <template #default="{ row }">
            <el-tooltip 
              :content="row.details" 
              placement="top" 
              :show-after="500"
            >
              <span class="details-text">{{ truncateText(row.details) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewDetails(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="救助详情"
      width="50%"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录ID">{{ currentRecord.recordId }}</el-descriptions-item>
        <el-descriptions-item label="救助站ID">{{ currentRecord.shelterId }}</el-descriptions-item>
        <el-descriptions-item label="动物ID">{{ currentRecord.animalId }}</el-descriptions-item>
        <el-descriptions-item label="救助时间">{{ formatDate(currentRecord.rescueTime) }}</el-descriptions-item>
        <el-descriptions-item label="救助详情" :span="2">
          {{ currentRecord.details }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh, Search } from '@element-plus/icons-vue';
import shelterService from '@/service/shelter.js';

const allRecords = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const currentRecord = ref({});
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 筛选数据
const filterData = reactive({
  recordId: '',
  shelterId: '',
  animalId: ''
});

// 计算当前页显示的记录
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  // 筛选逻辑：如果筛选条件有值，则先过滤 allRecords
  let filteredRecords = allRecords.value;
  if (filterData.recordId) {
    filteredRecords = filteredRecords.filter(r => String(r.recordId).includes(filterData.recordId));
  }
  if (filterData.shelterId) {
    filteredRecords = filteredRecords.filter(r => String(r.shelterId).includes(filterData.shelterId));
  }
  if (filterData.animalId) {
    filteredRecords = filteredRecords.filter(r => String(r.animalId).includes(filterData.animalId));
  }
  // 更新 total，注意：这会在每次分页或筛选时重新计算总数
  total.value = filteredRecords.length;
  // 对筛选后的结果进行分页
  return filteredRecords.slice(start, end);
});

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const truncateText = (text) => {
  if (!text) return '';
  return text.length > 50 ? text.substring(0, 50) + '...' : text;
};

const viewDetails = (record) => {
  currentRecord.value = record;
  dialogVisible.value = true;
};

const fetchAllRecords = async () => {
  loading.value = true;
  try {
    // 修改：调用 getRescueRecords 并传递大的 limit 来获取所有记录
    const response = await shelterService.getRescueRecords({ page: 1, limit: 10000 }); 
    if (response.code === 200) {
      // 注意：后端返回的数据结构可能依然是分页格式，比如在 response.data.records 或类似字段中
      // 这里假设所有记录直接在 response.data 中
      allRecords.value = response.data || []; // 存储所有数据
      // 如果后端返回的数据包含 total，我们应该使用那个 total
      // total.value = response.total || allRecords.value.length; 
      // 但由于我们现在是前端分页，total应该基于 allRecords 的长度
      total.value = allRecords.value.length;
      currentPage.value = 1; // 重置到第一页
      console.log('所有救助记录获取成功，总数:', total.value);
    } else {
      ElMessage.error(response.message || '获取救助记录失败');
      allRecords.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取救助记录失败:', error);
    ElMessage.error('获取救助记录失败，请稍后重试');
    allRecords.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1; // 切换每页数量时，回到第一页
};

const handleSearch = () => {
  currentPage.value = 1; // 每次搜索回到第一页
  // 触发 computed 属性重新计算
  // total.value 会在 paginatedRecords 计算属性中更新
};

const handleReset = () => {
  filterData.recordId = '';
  filterData.shelterId = '';
  filterData.animalId = '';
  currentPage.value = 1; // 重置到第一页
  // 触发 computed 属性重新计算
};

onMounted(() => {
  fetchAllRecords(); // 初始化时获取所有记录
});
</script>

<style scoped>
.rescue-records {
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

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.details-text {
  display: inline-block;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.filter-container {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.filter-form .el-form-item {
  margin-bottom: 0; /* 移除内联表单项的下边距 */
  margin-right: 15px;
}

:deep(.el-table) {
  border-radius: 4px;
}

:deep(.el-table__header) {
  background-color: #f5f7fa;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-descriptions) {
  padding: 20px;
}

:deep(.el-descriptions__label) {
  width: 120px;
  font-weight: bold;
  color: #606266;
}

:deep(.el-tooltip__trigger) {
  cursor: pointer;
}
</style>