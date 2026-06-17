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
            <el-table-column prop="shelterId" label="救助站ID" width="120" align="center" />           
            <el-table-column prop="userId" label="创建者ID" width="120" align="center"/>           
            <el-table-column prop="name" label="救助站名称" width="220" align="center" />  
            <el-table-column prop="address" label="救助站地址" width="210" align="center" />    
            <el-table-column prop="contactInfo" label="联系电话" width="220" align="center" />
        </el-table>
    </div>
    
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import service from '../../service/admin.js';
// import service from '../../service/user.js';
import { useRouter } from 'vue-router'
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
  getShelter()
});

//获取所有救助站申请记录
const getShelter = async () => {
    try {
        const response = await service.getShelters()
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

async function handleSearch() {
    if(searchGoodsName.value == ''){
        console.log("空空空空空空空空空空")
        ElMessage.error('关键词为空')
        getShelter()
        return ;
    }
    try {
        const response = await service.searchShelters(searchGoodsName.value)
        console.log(response)
        if (response.code === 200) {
            console.log('获取申请记录成功:', response.data)
            ElMessage.success('搜索成功！')
            applyData.value = response.data
        } else {
            console.error('获取申请记录失败:', response.message)
            ElMessage.error(response.message)
            applyData.value = []
        }
    } catch (error) {
        console.error('请求异常:', error)
        throw error
    }
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