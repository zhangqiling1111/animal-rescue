<template>
    <!-- Table列表展示 -->
    <div class="list">
        <el-table 
            :data="tableData" 
            stripe 
            v-loading="loading" 
            height="430px"
            style="width: 100%"
        >           
            <el-table-column label="动物照片" width="190" align="center">
                <template #default="scope">
                    <el-image 
                    :src="scope.row.photos" 
                    fit="cover" 
                    style="width: 120px; height: 120px; border-radius: 8px;"
                    :preview-src-list="[scope.row.photos]"
                    />
                </template>
            </el-table-column>
            <el-table-column prop="location" label="救助地址" width="200" align="center" />           
            <el-table-column label="上报时间" width="180" align="center">
                <template #default="scope">
                    {{ new Date(scope.row.reportedAt).toISOString().replace('T', ' ').substring(0, 19) }}
                </template>
            </el-table-column>
            <el-table-column prop="healthStatus" label="健康状态" width="200" align="center" />           
            <el-table-column label="处理状态" width="130" align="center">
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
        </el-table>
    </div>
</template>

<script setup lang="ts">
  import { ref, reactive, toRefs, onMounted } from 'vue';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import service from '../../service/user.js';
  const statusType = (status) => {
    switch (status) {
        case '待处理': return 'warning';
        case '已接收': return 'success';
        case '已拒绝': return 'danger';
        default: return 'primary';
    }
  };
  const tableData = ref([])
  // 获取申请领养流浪动物记录
  const fetchAnimals = async () => {
      try {
          const response = await service.getReportByUserId()
          console.log(response)
          if (response.code === 200) {
              console.log('获取申请记录成功:', response.data)
              const cleanedData = response.data.map(item => ({
                ...item,
                // photos: item.photos.replace(/^"|"$/g, '') // 处理单个字段
                }));
                tableData.value = cleanedData; // 赋值清理后的数据
            //   tableData.value = response.data
              // console.log('流浪动物信息：' + animal2.value[0].age)             
          } else {
              tableData.value =[]
              console.error('获取申请记录失败:', response.message)
          }
      } catch (error) {
          console.error('请求异常:', error)
          throw error
      }
  }
  const loading = ref(false);
  onMounted(() => {
    fetchAnimals()
  });

</script>

<style scoped lang="scss">
  
  .list {
      margin-top: 26px;
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