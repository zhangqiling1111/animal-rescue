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
            <!-- 用户ID -->
            <el-table-column prop="userId" label="用户ID" width="80" align="center" />
            
            <!-- 用户账号 -->
            <el-table-column prop="username" label="用户账号" width="140" align="center" />
            
            <!-- 用户邮箱 -->
            <el-table-column prop="email" label="用户邮箱" width="200" align="center" />
            
            <el-table-column prop="phone" label="联系方式" width="200" align="center" />

            <!-- 用户身份 -->
            <el-table-column label="用户身份" width="100" align="center">
                <template #default="scope">
                    {{ getChineseRole(scope.row.role) }}
                </template>
            </el-table-column>

            <!-- 操作列 -->
            <el-table-column label="操作" width="180" align="center" fixed="right">
                <template #default="scope">
                    <!-- 仅用户和救助站角色显示操作按钮 -->
                    <template v-if="['USER', 'SHELTER'].includes(scope.row.role)">
                        <!-- 未冻结时显示冻结按钮 -->
                        <el-button 
                            v-if="!scope.row.isFrozen" 
                            type="danger" 
                            size="small" 
                            @click="freezeUser(scope.row.userId)"
                        >
                            冻结账号
                        </el-button>
                        
                        <!-- 已冻结时显示解冻按钮 -->
                        <el-button 
                            v-else 
                            type="success" 
                            size="small" 
                            @click="unfreezeUser(scope.row.userId)"
                        >
                            解冻账号
                        </el-button>
                    </template>
                    <span v-else style="color: #dd2020">无操作权限</span>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script setup lang="ts">
    import { ref, reactive, toRefs, onMounted } from 'vue';
    import { ElMessage, ElMessageBox } from 'element-plus';
    import service from '../../service/admin.js';
    // import service from '../../service/user.js';
    const tableData = ref([])
    // 定义角色英文到中文的映射
    const roleMap = {
        'ADMIN': '管理员',
        'USER': '普通用户',
        'SHELTER': '救助站用户'
    };

    // 根据英文角色返回中文角色
    const getChineseRole = (role) => {
        return roleMap[role] || '未知角色';
    };
    // 冻结用户
    const freezeUser = async (userId) => {
        try {
            const response = await service.freezeUser(userId); // 调用冻结接口
            if (response.code === 200) {
                ElMessage.success('冻结成功');
                // 更新本地数据（或重新获取列表）
                tableData.value = tableData.value.map(user => 
                    user.userId === userId ? { ...user, isFrozen: true } : user
                );
            } else {
                ElMessage.error(response.message);
            }
        } catch (error) {
            ElMessage.error('网络请求失败，请重试');
        }
    };

    // 解冻用户
    const unfreezeUser = async (userId) => {
        try {
            const response = await service.unfreezeUser(userId); // 调用解冻接口
            if (response.code === 200) {
                ElMessage.success('解冻成功');
                // 更新本地数据（或重新获取列表）
                tableData.value = tableData.value.map(user => 
                    user.userId === userId ? { ...user, isFrozen: false } : user
                );
            } else {
                ElMessage.error(response.message);
            }
        } catch (error) {
            ElMessage.error('网络请求失败，请重试');
        }
    };
  
    // 获取申请领养流浪动物记录
    const fetchAnimals = async () => {
        try {
            const response = await service.getUsersInfo()
            console.log(response)
            if (response.code === 200) {
                console.log('获取申请记录成功:', response.data)

                tableData.value = response.data
                // console.log('流浪动物信息：' + animal2.value[0].age)
                
            } else {
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
    
    async function handleSearch() {
        
    }
    // async function cancelApplication(data) {
        
    // }

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