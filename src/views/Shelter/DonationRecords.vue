<template>
  <div class="donation-records">
    <div class="page-header">
      <h2>捐赠记录</h2>
      <p class="subtitle">查看救助站的所有捐赠记录</p>
    </div>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>捐赠记录列表</span>
          <el-button type="primary" @click="fetchDonations">
            <el-icon><Refresh /></el-icon>刷新
          </el-button>
        </div>
      </template>

      <div v-if="donations.length === 0" class="empty-state">
        <el-empty description="暂无捐赠记录" />
      </div>
      
      <el-table 
        v-else 
        :data="donations" 
        style="width: 100%"
        :stripe="true"
        :border="true"
        v-loading="loading"
      >
        <el-table-column prop="donationId" label="捐赠记录 ID" width="120" />
        <el-table-column prop="donateUserId" label="捐赠人 ID" width="120" />
        <el-table-column prop="shelterId" label="救助站 ID" width="120" />
        <el-table-column prop="amount" label="捐赠金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="itemType" label="物资类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getItemTypeTag(row.itemType)">
              {{ row.itemType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="donatedAt" label="捐赠时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.donatedAt) }}
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
          :page-size="10"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="捐赠详情"
      width="50%"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="捐赠记录ID">{{ currentRecord.donationId }}</el-descriptions-item>
        <el-descriptions-item label="捐赠人ID">{{ currentRecord.userId }}</el-descriptions-item>
        <el-descriptions-item label="救助站ID">{{ currentRecord.shelterId }}</el-descriptions-item>
        <el-descriptions-item label="捐赠金额">¥{{ currentRecord.amount }}</el-descriptions-item>
        <el-descriptions-item label="物资类型">{{ currentRecord.itemType }}</el-descriptions-item>
        <el-descriptions-item label="捐赠时间">{{ formatDate(currentRecord.donatedAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import shelterService from '@/service/shelter.js';

const donations = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const currentRecord = ref({});
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const getItemTypeTag = (type) => {
  const typeMap = {
    '现金': 'success',
    '食品': 'warning',
    '医疗用品': 'danger',
    '其他': 'info'
  };
  return typeMap[type] || 'info';
};

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

const viewDetails = (record) => {
  currentRecord.value = record;
  dialogVisible.value = true;
};

const fetchDonations = async () => {
  loading.value = true;
  try {
    const response = await shelterService.getDonationRecords({
      page: currentPage.value,
      limit: 10
    });
    if (response.code === 200) {
      donations.value = response.data;
      total.value = response.total;
    } else {
      ElMessage.error(response.message || '获取捐赠记录失败');
    }
  } catch (error) {
    console.error('获取捐赠记录失败:', error);
    ElMessage.error('获取捐赠记录失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchDonations();
};

onMounted(() => {
  fetchDonations();
});
</script>

<style scoped>
.donation-records {
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

.amount {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
</style>