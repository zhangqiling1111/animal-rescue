<template>
    <!-- Table列表展示 -->
    <div class="list">
        <el-table 
            :data="applyData" 
            stripe 
            v-loading="loading" 
            height="430px"
            style="width: 100%"
        >
            <el-table-column prop="shelterName" label="救助站名称" width="140" align="center" />           
            <el-table-column prop="address" label="救助站地址" width="150" align="center"/>           
            <el-table-column prop="contactInfo" label="联系电话" width="170" align="center" />
            <el-table-column label="申请时间" width="200" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.appliedAt) }}
              </template>
            </el-table-column>                    
            <el-table-column label="操作" width="220" align="center" fixed="right">
              <template #default="scope">
                  <!-- 待审核状态显示操作按钮 -->
                  <template v-if="scope.row.status === '待审核'">
                      <el-button 
                          type="success" 
                          size="small" 
                          @click="approveApplication(scope.row)"
                          style="margin-right: 8px"
                      >
                          通过申请
                      </el-button>
                      <el-button 
                          type="danger" 
                          size="small" 
                          @click="rejectApplication(scope.row)"
                      >
                          拒绝申请
                      </el-button>
                  </template>

                  <!-- 已通过状态 -->
                  <span v-else-if="scope.row.status === '已通过'" style="color: #67c23a">
                      已通过
                  </span>

                  <!-- 已拒绝状态 -->
                  <span v-else-if="scope.row.status === '已拒绝'" style="color: #dd2020">
                      已拒绝
                  </span>
              </template>
          </el-table-column>
        </el-table>
    </div>
    
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
// import service from '../../service/admin.js';
import service from '../../service/user.js';
import adminservice from '../../service/admin.js';
import { useRouter } from 'vue-router'
// 状态样式映射
const statusType = (status) => {
  const map = {
    '待审核': 'warning',
    '已拒绝': 'danger',
    '已通过': 'success'
  }
  return map[status] || 'info'
}
// 在 script 中定义格式化函数
const formatTime = (timeStr) => {
  // 1. 替换 T 为空格
  // 2. 截取前 19 个字符（保留到秒，去掉毫秒和时区）.replace('T', ' ').substring(0, 19)
  return timeStr.replace('T', ' ').substring(0, 19);
};
const applyData = ref([])

const searchGoodsName = ref('');
const loading = ref(false);

onMounted(() => {
  loading.value = false
  getShelterApplication()
});

//获取所有救助站申请记录
const getShelterApplication = async () => {
    try {
        // const response = await service.getShelterApplicationDetails()
        const response = await adminservice.getAllApplications()
        console.log(response)
        if (response.code === 200) {
            console.log('获取申请记录成功:', response.data)
            applyData.value = response.data
        } else {
            console.error('获取申请记录失败:', response.message)
        }
    } catch (error) {
        console.error('请求异常:', error)
        throw error
    }
}
// 审核通过处理
const approveApplication = async (row) => {
    try {
        const response = await adminservice.approveApplication(row.applicationId); // 假设接口需要申请ID
        if (response.code === 200) {
            ElMessage.success('审核通过');
            // 重新获取数据或更新当前行状态
            // row.status = '已通过'; // 临时更新本地状态（建议重新拉取数据）
            getShelterApplication()
        } else {
            ElMessage.error(response.message);
        }
    } catch (error) {
        ElMessage.error('网络请求失败，请重试');
    }
};

// 审核拒绝处理
const rejectApplication = async (row) => {
    try {
        const response = await adminservice.rejectApplication(row.applicationId); // 假设接口需要申请ID
        if (response.code === 200) {
            ElMessage.success('审核拒绝！');
            // row.status = '已拒绝'; // 临时更新本地状态（建议重新拉取数据）
            getShelterApplication()
        } else {
            ElMessage.error(response.message);
        }
    } catch (error) {
        ElMessage.error('网络请求失败，请重试');
    }
};
async function handleSearch() {
    
}

</script>

<style scoped lang="scss">


.list {
    margin-top: 25px;
    background-color: #fff;
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 0 10px 2px rgba(0, 0, 0, 0.1); /* 添加轻微的阴影效果 */
}
.el-image {
  transition: transform 0.3s;
}
.el-image:hover {
  transform: scale(1.05);
}

.el-tag {
  font-weight: 500;
  letter-spacing: 0.5px;
}

:deep(.el-table__row) td {
  transition: background-color 0.3s;
}
.page {
    display: flex;
    justify-content: right;
    margin-top: 30px
}

.sb {
    cursor: pointer;
    color: red;
}

</style>