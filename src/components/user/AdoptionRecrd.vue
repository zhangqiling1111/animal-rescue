<template>
    <!-- 搜索的面板 -->
    <div class="search-box">
        <el-input v-model="searchGoodsName" placeholder="请输入流浪动物品种" type="text" />
        <el-button class="search1" type="primary" icon="Search" @click="handleSearch">搜索</el-button> 
    </div>

    <!-- Table列表展示 -->
    <div class="list">
        <el-table 
            :data="tableData" 
            stripe 
            v-loading="loading" 
            height="430px"
            style="width: 100%"
            @row-click="handleRowClick"
        >
            <el-table-column prop="animal.animalId" label="动物ID" width="70" align="center" />
            
            <el-table-column label="动物照片" width="150" align="center">
                <template #default="scope">
                    <el-image 
                    :src="scope.row.animal.animalPhoto" 
                    fit="cover" 
                    style="width: 120px; height: 120px; border-radius: 8px;"
                    :preview-src-list="[scope.row.animal.animalPhoto]"
                    />
                </template>
            </el-table-column>

            <el-table-column prop="animal.breed" label="品种" width="100" align="center" />
            
            <!-- <el-table-column label="年龄" width="60" align="center">
                <template #default="scope">
                    {{ scope.row.animal.age }} 个月
                </template>
            </el-table-column> -->
            <el-table-column label="申请时间" width="180" align="center">
                <template #default="scope">
                    {{ new Date(scope.row.application.appliedAt).toISOString().replace('T', ' ').substring(0, 19) }}
                </template>
            </el-table-column>
            <el-table-column prop="animal.shelterName" label="救助站" width="160" align="center" />
            
            <el-table-column label="审核状态" width="110" align="center">
                <template #default="scope">
                    <el-tag 
                    :type="statusType(scope.row.application.status)"
                    effect="light"
                    round
                    >
                    {{ scope.row.application.status }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="操作" width="130" align="center" fixed="right">
                <template #default="scope">
                    <el-button 
                    v-if="['审核中', '待面试'].includes(scope.row.application.status)"
                    type="danger" 
                    size="small" 
                    @click.stop="cancelApplication(scope.row)"
                    >
                    取消申请
                    </el-button>
                    <span v-else style="color: #909399">已处理</span>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <!-- 动物详情弹窗 -->
    <el-dialog v-model="dialogVisible"  width="700px">
        <div v-if="currentItem" class="animal-detail">
            <div class="image-container"> <!-- 新增容器包裹图片和输入框 -->
                <div class="image-wrapper">
                    <el-image :src="currentItem.animal.animalPhoto" class="detail-image"
                        :preview-src-list="[currentItem.animal.animalPhoto]" style="width: 100%; height: 100%;" />
                </div>
                <p><span>申请时间：</span>{{ new Date(currentItem.application.appliedAt).toISOString().replace('T', ' ').substring(0, 19) }}</p>
                <p><span>备注：</span>{{ currentItem.application.notes }}</p>
            </div>

            <div class="detail-info">
                <p><span>动物ID：</span>{{ currentItem.animal.animalId }}</p>
                <p><span>品种：</span>{{ currentItem.animal.breed }}</p>
                <p><span>年龄：</span>{{ currentItem.animal.age }}个月</p>
                <p><span>健康状态：</span>{{ currentItem.animal.healthStatus }}</p>
                <p><span>所属救助站：</span>{{ currentItem.animal.shelterName }}</p>
                <p><span>救助站地址：</span>{{ currentItem.animal.address }}</p>
                <p><span>面试时间：</span>{{ formatAppliedAt(currentItem.application.interviewTime) }}</p>
            </div>
        </div>
    </el-dialog>
</template>

<script setup lang="ts">
  import { ref, reactive, toRefs, onMounted } from 'vue';
  import { ElMessage, ElMessageBox } from 'element-plus';
  // import service from '../../service/admin.js';
  import service from '../../service/user.js';
  import { useRouter } from 'vue-router'
  // 弹窗状态及当前行数据
  const dialogVisible = ref(false);
  const currentItem = ref(null);
  const statusType = (status) => {
    switch (status) {
        case '审核中': return 'warning';
        case '已通过': return 'success';
        case '已拒绝': return 'danger';
        default: return 'primary';
    }
  };
  const tableData = ref([])
  // 行点击事件处理
  const handleRowClick = (row) => {
    currentItem.value = row; // 保存当前行数据
    dialogVisible.value = true; // 显示弹窗
  };
  const formatAppliedAt = (time) => {
    if (!time) {
        return '暂未安排面试'; // 当时间数据为空时显示的默认信息
    }
    return new Date(time).toISOString().replace('T', ' ').substring(0, 19);
  };
  // 取消申请方法（示例）
  const cancelApplication = async (row) => {
      console.log('取消申请', row.application.applicationId);
      try {
          const response = await service.cancelAdoption(row.application.applicationId)
          console.log(response)
          if (response.code === 200) {
            ElMessage.success('取消成功！')
            fetchAnimals()
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
  // 获取申请领养流浪动物记录
  const fetchAnimals = async () => {
      try {
          const response = await service.getAdoptionDetails()
          console.log(response)
          if (response.code === 200) {
              console.log('获取申请记录成功:', response.data)

              tableData.value = response.data
              // console.log('流浪动物信息：' + animal2.value[0].age)
              
          } else {
              tableData.value = []
              console.error('获取申请记录失败:', response.message)
             
          }
      } catch (error) {
          console.error('请求异常:', error)
          throw error
      }
  }
  const searchGoodsName = ref('');
  const loading = ref(false);

  onMounted(() => {
    fetchAnimals()
  });
  //searchAdoptionApplicationsByBreed
  async function handleSearch() {
    if(searchGoodsName.value == ''){
        console.log("空空空空空空空空空空")
        ElMessage.error('关键词为空')
        fetchAnimals()
        return ;
    }
    try {
          const response = await service.searchAdoptionApplicationsByBreed(searchGoodsName.value)
          console.log(response)
          if (response.code === 200) {
            if (response.data[0].message) {
                console.error('获取申请记录失败:', response.data[0].message);
                ElMessage.error('搜索失败!')
                tableData.value = []
                return ;
            }
              console.log('获取申请记录成功:', response.data)
              ElMessage.success('搜索成功！')
              tableData.value = response.data
                          
          } else {
              console.error('获取申请记录失败:', response.message)
              
          }
      } catch (error) {
          console.error('请求异常:', error)
          throw error
      }
  }
  // async function cancelApplication(data) {
      
  // }

</script>

<style scoped lang="scss">
  .search-box {
      display: flex;
      align-items: center; /* 垂直居中对齐 */
      background-color: #ffffff; /* 白色背景 */
      margin-bottom: 10px;
      padding: 10px 30px; /* 调整内边距，保持大小不变但更加美观 */
      border-radius: 10px;
      box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1); /* 添加轻微的阴影效果 */
  
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
  .animal-detail {
        display: flex;
        gap: 20px;
    }

    .image-container {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
    }

    .image-wrapper {
        width: 300px;
        height: 300px;
        flex-shrink: 0;
        border-radius: 8px;
        overflow: hidden;
        margin-bottom: 15px;
        /* 关键：裁剪超出部分 */

        .el-image__img {
            // 覆盖Element Plus的图片容器样式
            width: 100% !important;
            height: 100% !important;
            object-fit: cover !important;
            /* 核心：按比例填充并裁剪 */
            object-position: center !important;
            /* 可选：调整裁剪区域（默认居中） */
        }
    }
    /* 原有图片样式保持不变 */
    .detail-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s;
    }

    .detail-image:hover {
        transform: scale(1.05);
    }
    .detail-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }

    .dialog-actions {
        margin-top: 20px;
        text-align: right;
    }
    .detail-info p {
        margin: 12px 0;
        padding: 8px 0;
        border-bottom: 1px dashed #ebeef5;

        span {
            color: #909399;
            min-width: 100px;
            display: inline-block;
            font-weight: 500;
        }
    }
    .image-container p {
        margin: 12px 10px;
        padding: 8px 0;
        border-bottom: 1px dashed #ebeef5;

        span {
            color: #909399;
            min-width: 100px;
            display: inline-block;
            font-weight: 500;
        }
    }
</style>