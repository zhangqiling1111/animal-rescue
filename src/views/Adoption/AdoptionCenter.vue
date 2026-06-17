<template>
    <div class="home-container">

        <!-- 轮播图和信息栏 -->
        <div class="info-container">
            <!-- 左侧新闻资讯 -->
            <div class="info-box news-box">
                <h3>📰 最新相关资讯</h3>
                <ul>
                    <li v-for="(news, index) in newsList" :key="index" class="news-item">
                        <a :href="news.url" target="_blank" class="news-link">
                            <!-- <el-icon><el-icon-arrow-right /></el-icon> -->
                            <span>{{ news.title }}</span>
                        </a>
                    </li>
                </ul>
            </div>

            <!-- 轮播图 -->
            <el-carousel class="main-carousel" height="300px" :interval="5000">
                <el-carousel-item v-for="(animals2, index) in featuredAnimals" :key="index">
                    <img :src="animals2.animalPhoto" class="carousel-image" />
                </el-carousel-item>
            </el-carousel>

            <!-- 右侧救助指南 -->
            <div class="info-box news-box">
                <h3>📖 流浪动物救助指南</h3>
                <ul>
                    <li v-for="(news, index) in rescueGuides" :key="index" class="news-item">
                        <a :href="news.url" target="_blank" class="news-link">
                            <!-- <el-icon><el-icon-arrow-right /></el-icon> -->
                            <span>{{ news.title }}</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <!-- 搜索栏 -->
        <div class="search-bar">
            <form @submit.prevent="handleSearch">
                <div class="form-row">
                    <!-- 品种搜索 -->
                    <div class="form-group">
                        <label for="breed">品种</label>
                        <input v-model="searchForm.breed" id="breed" type="text" placeholder="请输入品种（如：金毛犬）" />
                    </div>
                    <!-- 地区搜索 -->
                    <div class="form-group">
                        <label for="area">地区</label>
                        <input v-model="searchForm.area" id="area" type="text" placeholder="请输入地区（如：北京市）" />
                    </div>
                    <!-- 健康状况 -->
                    <div class="form-group">
                        <label for="healthStatus">健康状况</label>
                        <input v-model="searchForm.healthStatus" id="healthStatus" type="text"
                            placeholder="请输入健康状况（如：健康）" />
                    </div>
                </div>
                <div class="form-row">
                    <!-- 是否可领养 -->
                    <div class="form-group">
                        <label for="isAdoptable">是否可领养</label>
                        <select v-model="searchForm.isAdoptable" id="isAdoptable">
                            <option value="">全部</option>
                            <option value="1">可领养</option>
                            <option value="0">不可领养</option>
                        </select>
                    </div>
                    <!-- 收容所名称 -->
                    <div class="form-group ">
                        <label for="shelterName">收容所名称</label>
                        <input v-model="searchForm.shelterName" id="shelterName" type="text"
                            placeholder="请输入收容所名称（如：希望之家）" />
                    </div>
                    <div class="button-group">
                        <button type="button" @click="handleReset">重置条件</button>
                        <button type="button" @click="handleSearch">立即搜索</button>
                    </div>
                </div>
            </form>
        </div>
        <!-- 动物列表 -->
        <main class="main-content">
            <el-row :gutter="20" class="animal-grid">
                <el-col v-for="animals2 in filteredAnimals" :key="animals2.animalId" :xs="12" :sm="8" :md="24 / 5"
                    :lg="24 / 5" :xl="24 / 5" @click="showAnimalDetail(animals2)">
                    <div class="animal-item">
                        <el-image :src="animals2.animalPhoto" fit="cover" class="animal-image">
                            <template #error>
                                <div class="image-error">图片加载失败</div>
                            </template>
                        </el-image>
                        <div class="animal-info">
                            <h4>{{ animals2.breed }}</h4>
                            <p>{{ animals2.shelterName }}</p>
                            <p>{{ animals2.age }}个月</p>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <div class="pagination-container">
                <el-pagination background :current-page.sync="currentPage" :page-size="pageSize" :disabled="isLoading"
                    :total="total" :hide-on-single-page=false layout="prev, pager, next"
                    @current-change="handleCurrentChange" class="pagination" />
            </div>
        </main>
        <!-- 动物详情弹窗 -->
        <el-dialog v-model="detailVisible" :title="currentAnimal?.breed" width="700px">
            <div v-if="currentAnimal" class="animal-detail">
                <div class="image-container"> <!-- 新增容器包裹图片和输入框 -->
                    <div class="image-wrapper">
                        <el-image :src="currentAnimal.animalPhoto" class="detail-image"
                            :preview-src-list="[currentAnimal.animalPhoto]" style="width: 100%; height: 100%;" />
                    </div>
                    <el-input v-model="adoptReason" class="image-input" placeholder="在此输入领养原因"
                        style="width: 300px; margin-top: 10px;" />
                </div>

                <div class="detail-info">
                    <p><span>动物ID：</span>{{ currentAnimal.animalId }}</p>
                    <p><span>品种：</span>{{ currentAnimal.breed }}</p>
                    <p><span>年龄：</span>{{ currentAnimal.age }}个月</p>
                    <p><span>健康状态：</span>{{ currentAnimal.healthStatus }}</p>
                    <p><span>救助站ID：</span>{{ currentAnimal.shelterId }}</p>
                    <p><span>所属救助站：</span>{{ currentAnimal.shelterName }}</p>
                    <p><span>救助站地址：</span>{{ currentAnimal.address }}</p>                  
                    <div class="dialog-actions">
                        <el-button type="primary" @click="handleAdopt">申请领养</el-button>
                        <el-button @click="detailVisible = false">关闭</el-button>
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>

    <!-- 底部 -->
    <footer class="footer">
        <div class="footer-content">
            <!-- 领养流程（保留原有） -->
            <div class="footer-section">
                <h4>领养流程</h4>
                <ul>
                    <li>提交领养申请</li>
                    <li>等待审核通知</li>
                    <li>审核资格材料</li>
                    <li>安排线下见面</li>
                    <li>签署领养协议</li>
                    <li>生成领养凭证</li>
                </ul>
            </div>

            <!-- 联系我们（补充地址和工作时间） -->
            <div class="footer-section">
                <h4>联系我们</h4>
                <p>电话：400-1234-5678</p>
                <p>邮箱：adopt@animal.com</p>
                <p>地址：桂林市七星区流浪动物保护中心</p>
                <p>工作时间：周一至周五 9:00-18:00</p>
            </div>

            <!-- 社交媒体（新增） -->
            <div class="footer-section">
                <h4>关注我们</h4>
                <!-- 二维码图片 -->
                <div class="qrcode-container">
                    <img src="../../assets/交流.png" alt="平台二维码" class="qrcode-image" />
                </div>
                <!-- 图片链接 -->
                <div class="social-image-links">
                    <a href="#" class="social-link">
                        <img src="../../assets/微信.png" alt="微信" class="social-icon" />
                    </a>
                    <a href="#" class="social-link">
                        <img src="../../assets/QQ.png" alt="QQ" class="social-icon" />
                    </a>
                    <a href="#" class="social-link">
                        <img src="../../assets/facebook.png" alt="facebook" class="social-icon" />
                    </a>
                    <a href="#" class="social-link">
                        <img src="../../assets/微博.png" alt="微博" class="social-icon" />
                    </a>
                </div>
            </div>
        </div>
        <!-- 版权信息（新增） -->
        <div class="copyright">
            <p>© 2025 流浪动物救助平台 版权所有 | 备案号：24124422421号</p>
            <p>致力于为流浪动物提供温暖的家，让每个生命都值得被善待</p>
        </div>

    </footer>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { Search } from '@element-plus/icons-vue'
    
    import { ElPagination } from 'element-plus'; // 新增组件引入
    import service from '../../service/user.js'
    // import axios from '../../utils/axios'
    import { ElMessage } from 'element-plus'
    const animals2 = ref([])
    const adoptReason = ref('');
    const newsList = ref([
        { title: '我国每年新增超千万只流浪...', url: 'https://www.sohu.com/a/866559079_100001695' },
        { title: '善待动物关爱生命，生生不...', url: 'https://baijiahao.baidu.com/s?id=1816222693472218341' },
        { title: '流浪狗问题难以解决：数量...', url: 'https://baijiahao.baidu.com/s?id=1824177606054439458' },
        { title: '从流浪到归宿，生生不息让...', url: 'https://news.sina.com.cn/sx/2025-02-25/detail-inemswwc1197787.shtml' },

    ])
    const rescueGuides = ref([
        { title: '流浪动物民间救助站之难，...', url: 'https://zhuanlan.zhihu.com/p/61264248' },
        { title: '动物救助实用指南，如何学...', url: 'https://www.cvma.org.cn/6956/202111/52107.html' },
        { title: '一份详细的流浪动物救助指...', url: 'https://www.sohu.com/a/765727745_100166956' },
        { title: '爱心人士看过来，最细节的...', url: 'https://www.sohu.com/a/770336582_121679342' },

    ])
    // 定义分页相关变量
    const total = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(15);
    const isLoading = ref(false); // 加载状态（防止重复请求）
    const hasMore = ref(true); // 是否有下一页（需后端配合判断）
    // 搜索条件响应式数据（与 SQL 条件完全对应）
    const searchForm = ref({
        breed: '',          // 品种
        area: '',           // 地区
        healthStatus: '',   // 健康状况
        isAdoptable: '',  // 是否可领养（1/0/-1）
        shelterName: ''     // 收容所名称
    });

    // 搜索方法
    const handleSearch = async () => {
        currentPage.value = 1
        if (searchForm.value.isAdoptable == '') {
            if (searchForm.value.breed == '' && searchForm.value.area == ''
                && searchForm.value.healthStatus == '' && searchForm.value.shelterName == ''
            ) {
                ElMessage.error('关键词为空')
                fetchAnimals()
                return;
            }
        }
        
        // 调用后端接口时传递 searchForm 作为参数
        // 例如：await getAnimals(searchForm.value);
        console.log('搜索条件：', searchForm.value);
        try {
            const response = await service.searchAnimals(searchForm.value)

            console.log(response)

            if (response.code === 200) {
                if(response.data == null){
                    animals2.value = [];
                    ElMessage.error(response.message)
                    return
                }
                console.log('获取动物数据成功:', response.data)
                animals2.value = response.data
                ElMessage.success('搜索成功！');
                
                // console.log('流浪动物信息：' + animal2.value[0].age)
                return response.data
            } else {
                ElMessage.error(response.message)
                console.error('获取数据失败:', response.message)
                animals2.value = []
            }
        } catch (error) {
            console.error('请求异常:', error)
            throw error
        }
    };

    // 重置条件
    const handleReset = () => {
        searchForm.value = {
            breed: '',
            area: '',
            healthStatus: '',
            isAdoptable: '',
            shelterName: ''
        };
    };
    

    const searchKeyword = ref('')
    const detailVisible = ref(false)
    const currentAnimal = ref(null)
    
    // 精选推荐动物（轮播图用）
    const featuredAnimals = computed(() => animals2.value.slice(0, 3))
    //获取流浪动物总数 getAnimalTotalNum
    const gettotal = async () => {
        try {
            const response = await service.getAnimalTotalNum();
            if (response.code === 200) {
                // 假设后端返回空数组表示无更多数据
                total.value = response.data.animalTotalNum;
            }
        } catch (error) {
            console.error('请求异常:', error);
        } finally {
            isLoading.value = false;
        }
    };
    // 获取流浪动物信息
    const fetchAnimals = async () => {
        if (isLoading.value || !hasMore.value) return; // 加载中或无更多数据时不请求
        isLoading.value = true;
        try {
            const response = await service.getAnimals(currentPage.value, pageSize.value);
            if (response.code === 200) {
                // 假设后端返回空数组表示无更多数据
                if(response.data == null){
                    animals2.value = [];
                }
                hasMore.value = response.data.length > 0;
                animals2.value = response.data;
            } else {
                animals2.value = [];
                hasMore.value = false;
            }
        } catch (error) {
            console.error('请求异常:', error);
        } finally {
            isLoading.value = false;
        }
    };
    
    // 监听当前页码变化
    const handleCurrentChange = (newPage) => {
        currentPage.value = newPage;
        fetchAnimals();
    };

    // 过滤逻辑
    const filteredAnimals = computed(() => {
        return animals2.value.filter(animals2 =>
            animals2.shelterName.includes(searchKeyword.value) ||
            animals2.breed.includes(searchKeyword.value))
    })

    //申请领养方法
    const handleAdopt = async()=>{
        try {
            const animalId = currentAnimal.value?.animalId;
            const response = await service.adopt(animalId,adoptReason.value)
            console.log(response)
            if (response.code === 200) {
                ElMessage.success('申请成功！');
                console.log('申请成功！', response.data)
                detailVisible.value = false 
                // animals2.value = response.data
                // console.log('流浪动物信息：' + animal2.value[0].age)               
            } else {
                ElMessage.error(response.message)
                console.error('申请失败:', response.message)               
            }
        } catch (error) {
            console.error('请求异常:', error)
            throw error
        }
    }
    
    //领养弹窗
    const showAnimalDetail = (animals2) => {
        currentAnimal.value = animals2
        detailVisible.value = true
    }

    onMounted(() => {     
        gettotal()  
        fetchAnimals()
    })
</script>

<style scoped lang="scss">
@import url('https://cdn.tailwindcss.com');
    .home-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0px 20px;
    }

    .search-bar {
        padding: 20px;
        background: #fff;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
        // display: flex;
        margin: 20px 0;
        // justify-content: center;
        border-radius: 8px;
        /* 表单行样式 */
        .form-row {
            padding-left: 35px;
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin-bottom: 20px;
        }

        /* 表单组样式 */
        .form-group {
            flex: 1;
            max-width: 300px;
            margin-right: 60px;
        }

        /* 宽表单组样式（用于收容所名称） */
        .form-group-wide {
            flex: 2;
        }

        /* 标签样式 */
        label {
            display: block;
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            padding-left: 10px;
        }

        /* 输入框和选择框样式 */
        input{
            height: 40px;
            width: 300px;
            padding:0px 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
            transition: border-color 0.3s ease;
        }
        select {
            width: 322px;
            height: 40px;
            padding:10px 0px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
            transition: border-color 0.3s ease;
        }

        /* 输入框和选择框聚焦样式 */
        input:focus,
        select:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
        }

        /* 按钮组样式 */
        .button-group {
            display: flex;
            justify-content: flex-end;
            // justify-items: center;
            gap: 10px;
        }

        /* 按钮样式 */
        button {
            height: 40px;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            transition: background-color 0.3s ease;
            margin-top: 25px;
            margin-left: 30px;
        }

        /* 重置按钮样式 */
        button[type="button"]:first-child {
            background-color: #f8f9fa;
            color: #333;
            border: 1px solid #ccc;
        }

        /* 重置按钮悬停样式 */
        button[type="button"]:first-child:hover {
            background-color: #e2e6ea;
        }

        /* 搜索按钮样式 */
        button[type="button"]:last-child {
            background-color: #007bff;
            color: #fff;
        }

        /* 搜索按钮悬停样式 */
        button[type="button"]:last-child:hover {
            background-color: #0069d9;
        }
    }

    .info-container {
        display: grid;
        grid-template-columns: 1fr 2fr 1fr;
        gap: 20px;
        margin: 30px 0;

        .info-box {
            background: #fff;
            // padding: 10px;
            padding-left: 20px;
            padding-top: 15px;
            border-radius: 8px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);

            h3 {
                color: #1a73e8;
                margin-bottom: 15px;
            }

            ul,
            ol {
                padding-left: 20px;

                li {
                    margin: 15px 0;
                    line-height: 1.6;
                }
            }
        }

        .main-carousel {
            
            .carousel-image {
                width: 100%;
                height: 300px;
                object-fit: cover;
                border-radius: 10px;
                // box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
            }
        }
    }

    /* 新闻资讯美化 */
    .news-box {
        // box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
        .news-item {
            margin: 12px 0;
            padding: 2px 10px;
            border-radius: 6px;
            transition: background 0.3s;

            &:hover {
                background: #f5f7fa;
            }
        }

        .news-link {
            display: flex;
            align-items: center;
            /* 新增垂直居中 */
            color: #606266;
            text-decoration: none !important;
            line-height: 1.5;
            /* 统一行高 */

            .el-icon {
                color: #5d9bed;
                margin-right: 0px;
                font-size: 14px;
                flex-shrink: 0;
                /* 防止图标被压缩 */
                position: relative;
                top: 1px;
                /* 微调图标位置 */
            }

            span {
                flex: 1;
                transition: color 0.3s;
                // padding: 2px 0; /* 增加文字上下间距 */
                padding-bottom: 12px;
                // align-items: center;
            }

        }
    }

    /* 新增栅格容器样式，解决小数列的布局问题 */
    .animal-grid {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-start;

        .el-col {
            /* 强制每个 item 占 20% 宽度（5项×20%=100%） */
            flex: 0 0 20%;
            max-width: 20%;
            /* 重置栅格系统的默认宽度，避免冲突 */
            width: 20% !important;
        }
    }

    .animal-item {
        margin: 0 10px 20px;
        /* gutter 为 20px，单边 10px */
        width: 100%;
        /* 占满父容器 */
        background: #fff;
        border-radius: 8px;
        overflow: hidden;
        cursor: pointer;
        transition: transform 0.3s;
        margin-bottom: 20px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

        &:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        }

        .animal-image {
            width: 100%;
            height: 200px;
        }

        .animal-info {
            padding: 15px;
            text-align: center;

            h4 {
                color: #303133;
                margin: 10px 0;
            }

            p {
                color: #606266;
                font-size: 14px;
                margin: 5px 0;
            }
        }
    }

    /* 弹窗图片优化 */
    .animal-detail {
        // background: linear-gradient(180deg, #e5f3fe, #ffffff);
        display: flex;
        gap: 20px;
    }

    .image-container {
        display: flex;
        flex-direction: column;
        align-items: center;
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
    :deep(.el-dialog) {
        background: linear-gradient(180deg, #e5f3fe, #ffffff);
        border-radius: 8px;
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
    .image-input {
        width: 300px;
        height: 40px;
        border: 1px solid #ebeef5;
        border-radius: 8px;
        // margin-top: 10px;
        // padding: 0 12px;
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
    .image-error {
        height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: #909399;
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
    .pagination-container {
        display: grid;
        place-items: center;
        margin-top: 20px; /* 可根据需要调整间距 */
    }
    .footer {
        background: #333;
        color: #fff;
        padding: 10px 0 10px; /* 增加底部 padding */
        margin-top: 3rem;

        .footer-content {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between; /* 改为两端对齐 */
            padding: 0 20px;

            .footer-section {
                padding-left: 100px;
                flex: 1; /* 平均分配空间 */
                min-width: 200px; /* 防止过小 */

                h4 {
                    color: #eee; /* 暖橙色，呼应公益主题 */
                    margin-bottom: 1rem;
                    font-size: 1.1rem;
                }

                p {
                    line-height: 2.4;
                    font-size: 0.95rem;
                }

                ul {
                    list-style: none;
                    padding: 0;

                    li {
                        margin: 0.8rem 0;
                        font-size: 0.95rem;
                        color: #eee;

                        a {
                            color: #eee;
                            text-decoration: none;
                            transition: color 0.3s;

                            &:hover {
                                color: #ff7f50;
                            }
                        }
                    }
                }
            }

            

            .social-links {
                display: flex;
                gap: 15px;
                margin-top: 1rem;

                a {
                    color: #fff;
                    font-size: 1.2rem;
                    transition: color 0.3s;

                    &:hover {
                        color: #ff7f50;
                    }
                }
            }
            .qrcode-container {
                // text-align: center;
                margin: 1rem 0;
            
                .qrcode-image {
                    width: 140px;
                    height: 140px;
                    border-radius: 8px;
                    border: 2px solid #fff;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }
            }
            
            .social-image-links {
                display: flex;
                // justify-content: center;
                gap: 0.5rem;
            
                .social-link {
                    display: inline-block;
                    transition: transform 0.3s;
            
                    &:hover {
                        transform: scale(1.1);
                    }
                }
            
                .social-icon {
                    width: 30px;
                    height: 30px;
                    object-fit: contain;
                    filter: invert(100%);
                    /* 白色图标适配深色背景，可根据实际图片颜色调整 */
                }
            }
        }
        
        .copyright {
            text-align: center;
            margin-top: 2rem;
            // padding: 1rem 20px;
            font-size: 0.9rem;
            color: #ccc;
        
            p:first-child {
                margin-bottom: 0.5rem;
            }
        }
    }
</style>