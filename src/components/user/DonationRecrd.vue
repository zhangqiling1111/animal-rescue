<template>
    <!-- 搜索的面板 -->
    <!-- Table列表展示 -->
    <div class="list">
        <el-table 
            :data="applyData" 
            stripe 
            v-loading="loading" 
            height="430px"
            style="width: 100%"
        >           
            <el-table-column prop="shelterId" label="救助站ID" width="90" align="center"/>  
            <el-table-column prop="shelterName" label="救助站名称" width="140" align="center"/>  
            <el-table-column prop="shelterAddress" label="救助站地址" width="140" align="center"/>
            <el-table-column prop="shelterContactInfo" label="救助站联系方式" width="170" align="center"/> 
            <el-table-column prop="amount" label="捐赠数量" width="80" align="center" />
            <el-table-column prop="itemType" label="捐赠类型" width="120" align="center" />
            <el-table-column label="捐赠时间" width="180" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.donatedAt) }}
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
// 在 script 中定义格式化函数
const formatTime = (timeStr) => {
  // 1. 替换 T 为空格
  // 2. 截取前 19 个字符（保留到秒，去掉毫秒和时区）
  return timeStr.replace('T', ' ').substring(0, 19);
};
const applyData = ref([])
const loading = ref(false);

onMounted(() => {
  loading.value = false
  getShelterApplication()
});

//获取所有捐赠记录
const getShelterApplication = async () => {
    try {
        const response = await service.getPersonalRecords()
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