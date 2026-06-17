<template>
    <!-- 搜索的面板 -->
    <div class="search-box">
        <el-input v-model="searchGoodsName" placeholder="请输入救助站名称" type="text" />
        <el-button class="search1" type="primary" icon="Search" @click="handleSearch">搜索</el-button> 
    </div>

    <!-- Table列表展示 -->
    <div class="list">
        <el-table 
            :data="applyData" 
            stripe 
            v-loading="loading" 
            height="430px"
            style="width: 100%"
        >
            <el-table-column prop="shelterName" label="救助站名称" width="130" align="center" />           
            <el-table-column prop="address" label="救助站地址" width="130" align="center"/>           
            <el-table-column prop="contactInfo" label="联系电话" width="150" align="center" />
            <el-table-column label="申请时间" width="180" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.appliedAt) }}
              </template>
            </el-table-column>                    
            <el-table-column label="审核状态" width="130" align="center">
            <template #default="scope">
                <el-tag 
                :type="statusType(scope.row.status)"
                effect="light"
                round
                >
                {{ scope.row.status }}
                </el-tag>
            </template>
            </el-table-column>

            <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="scope">
                <el-button 
                v-if="['待审核'].includes(scope.row.status)"
                type="danger" 
                size="small" 
                @click="cancelApplication(scope.row)"
                >
                取消申请
                </el-button>
                <span v-else-if="['已拒绝'].includes(scope.row.status)" style="color: #dd2020">申请失败</span>
                <span v-else style="color: #909399">成功申请</span>
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
  // 2. 截取前 19 个字符（保留到秒，去掉毫秒和时区）
  return timeStr.replace('T', ' ').substring(0, 19);
};
const applyData = ref([])

const searchGoodsName = ref('');
const loading = ref(false);

onMounted(() => {
  loading.value = false
  getShelterApplication()
});
// 取消申请方法（示例）
  const cancelApplication = async (row) => {
    
      console.log('取消申请', row.applicationId);
      try {
          const response = await service.cancelShelterApplication(row.applicationId)
          console.log(response)
          if (response.code === 200) {
            ElMessage.success('取消成功！')
            applyData.value = []
            // getShelterApplication()
            // fetchAnimals()
          } else {
            console.error('获取数据失败:', response.message)
            ElMessage.error(response.message)
          }
      } catch (error) {
          console.error('请求异常:', error)
          throw error
      }
      
      // 调用接口逻辑
  };
//获取所有救助站申请记录
const getShelterApplication = async () => {
    try {
        const response = await service.getShelterApplicationDetails()
        console.log(response)
        if (response.code === 200) {
            console.log('获取申请记录成功:', response.data)
            applyData.value = [response.data]
        } else {
            console.error('获取申请记录失败:', response.message)
        }
    } catch (error) {
        console.error('请求异常:', error)
        throw error
    }
}

async function handleSearch() {
    
}

</script>

<style scoped lang="scss">
.search-box {
    display: flex;
    align-items: center; /* 垂直居中对齐 */
    background-color: #ffffff; /* 白色背景 */
    margin-bottom: 10px;
    padding: 10px 30px; /* 调整内边距，保持大小不变但更加美观 */
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 添加轻微的阴影效果 */
 
    .el-input {
        width: 60%;
        margin-right: 40px; /* 调整输入框与按钮之间的间距 */
        border: 1px solid #dcdcdc; /* 边框颜色调整为淡灰色 */
        border-radius: 4px; /* 圆角边框 */
        padding: 10px; /* 内边距调整，使输入框看起来更加饱满 */
        box-sizing: border-box; /* 确保内边距和边框不会增加元素的总宽度 */
    }
 
    .el-button {
        /* 统一按钮样式 */
        padding: 10px 20px;
        border-radius: 4px;
        box-sizing: border-box;
        background-color: #409eff; /* 稍微调整红色，使其更加鲜明但不刺眼 */
        border-color: #409eff;
        color: #ffffff; /* 字体颜色为白色 */
 
        &:hover {
            background-color: #61abf5; /* 悬停时颜色稍微变浅 */
            border-color: #61abf5;
        }
    }
    .back{
        margin-left: 130px;
    }
}

.list {
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