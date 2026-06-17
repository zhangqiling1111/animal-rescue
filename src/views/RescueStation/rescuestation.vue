<template>
  <div class="shelter-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-container">
        <el-input v-model="searchKeyword" placeholder="搜索救助站名称或地址" clearable class="search-input">
          <template #prefix>
            <el-icon><el-icon-search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" class="search-btn" @click="handleSearch">
          立即搜索
        </el-button>
        <el-button type="success" class="apply-btn" @click="handleApply">
          申请成为救助站
        </el-button>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧救助站列表 -->
      <div class="shelter-list">
        <el-skeleton :loading="loading" animated :count="3">
          <template #template>
            <el-skeleton-item variant="text" style="width: 30%" />
            <el-skeleton-item variant="text" />
            <el-skeleton-item variant="text" style="width: 60%" />
          </template>
          <template #default>
            <el-scrollbar>
              <div v-for="Shelters in filteredShelters" :key="Shelters.shelterId" class="shelter-item"
                :class="{ 'active': activeShelter === Shelters.shelterId }" @click="activeShelter = Shelters.shelterId">
                <div class="shelter-info-wrapper">
                  <h3 class="shelter-name">{{ Shelters.name }}</h3>
                  <div class="shelter-info">
                    <span>地址：{{ Shelters.address }}</span>
                  </div>
                  <div class="shelter-info">
                    <span>联系方式：{{ Shelters.contactInfo }}</span>
                  </div>
                </div>
                <el-button type="success" class="donation-btn" @click="handleDonation(Shelters.shelterId)">
                  捐赠
                </el-button>
                <el-button type="success" class="report-btn" @click="handleReport(Shelters.shelterId)">
                  救助上报
                </el-button>
              </div>
            </el-scrollbar>
          </template>
        </el-skeleton>

        <div v-if="error" class="error-message">
          <el-alert type="error" :closable="false">
            {{ error }}
          </el-alert>
        </div>
      </div>

      <!-- 右侧新闻资讯 -->
      <div class="news-sidebar">
        <h3 class="sidebar-title">最新相关资讯</h3>
        <div class="news-list">
          <div class="news-item" v-for="(news, index) in newsList" :key="index">
            <a class="news-title" :href="news.url" target="_blank" rel="noopener noreferrer">
              {{ news.title }}
              <el-icon><el-icon-link /></el-icon>
            </a>
            <p class="news-content">
              {{ truncateContent(news.content, 50) }}
              <a v-if="news.content.length > 50" :href="news.url" class="read-more" target="_blank">
                查看更多...
              </a>
            </p>
            <div class="news-meta">
              <span class="news-date">{{ news.date }}</span>
              <span class="news-source">{{ news.source }}</span>
            </div>
          </div>
        </div>
      </div>

    </div>
    <!-- 流浪动物救助上报对话框 -->
    <el-dialog class="report-dialog" title="流浪动物救助上报" v-model="reportDialogVisible" width="30%">
      <el-form :model="reportForm" :rules="reportRules" ref="reportFormRef" class="report-form">

        <el-form-item label="发现位置" prop="location">
          <el-input v-model="reportForm.location"></el-input>
        </el-form-item>
        <el-form-item label="健康状况" prop="healthStatus">
          <el-input v-model="reportForm.healthStatus"></el-input>
        </el-form-item>
        <el-form-item label="动物照片" prop="rescuePhoto">
          <el-upload class="upload-demo" ref="uploadRef" :action="uploadUrl" :show-file-list="false"
            :before-upload="beforeUpload">
            <el-button type="primary" class="photo-btn">选择照片</el-button>
          </el-upload>
        </el-form-item>
        <div slot="footer" class="dialog-footer">
          <el-button class="cancel-btn" @click="reportDialogVisible = false">取消</el-button>
          <el-button class="submit-btn" type="primary" @click="handleReportSubmit">提交</el-button>
        </div>

      </el-form>
    </el-dialog>
    <!-- 救助站申请对话框 -->
    <el-dialog class="report-dialog" title="救助站申请" v-model="applyDialogVisible" width="30%">
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" class="report-form">
        <el-form-item label="救助站名称" prop="shelterName">
          <el-input v-model="applyForm.shelterName"></el-input>
        </el-form-item>
        <el-form-item label="救助站地址" prop="address">
          <el-input v-model="applyForm.address"></el-input>
        </el-form-item>
        <el-form-item label="救助站联系方式" prop="contactInfo">
          <el-input v-model="applyForm.contactInfo"></el-input>
        </el-form-item>

        <div slot="footer" class="dialog-footer">
          <el-button class="cancel-btn" @click="applyDialogVisible = false">取消</el-button>
          <el-button class="submit-btn" type="primary" @click="handleapplySubmit">提交</el-button>
        </div>

      </el-form>
    </el-dialog>
    <!-- 捐赠对话框 -->
    <el-dialog class="report-dialog" title="捐赠物资/资金" v-model="DonationDialogVisible" width="30%">
      <el-form :model="DonationForm" :rules="DonationFormRules" ref="DonationFormRef" class="report-form">
        <el-form-item label="捐赠数量" prop="location">
          <el-input v-model="DonationForm.amount"></el-input>
        </el-form-item>
        <el-form-item label="捐赠类型" prop="photos">
          <el-input v-model="DonationForm.itemType"></el-input>
        </el-form-item>
        <div slot="footer" class="dialog-footer">
          <el-button class="cancel-btn" @click="DonationDialogVisible = false">取消</el-button>
          <el-button class="submit-btn" type="primary" @click="handleDonationSubmit">捐赠</el-button>
        </div>
      </el-form>
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

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import axios from 'axios'
  import service from '../../service/user.js'
  import { ElMessage } from 'element-plus';
  const loading = ref(true)
  const error = ref('')
  const Shelters = ref([])
  
  const searchKeyword = ref('')
  const activeShelter = ref<number | null>(null)
  // 控制对话框显示隐藏applyDialogVisible
  const reportDialogVisible = ref(false);
  const DonationDialogVisible = ref(false);
  const applyDialogVisible = ref(false);
  const uploadRef = ref(null);
  const uploadUrl = '/rescue/summitReport'; // 上传接口地址
  // 上传前处理
  const beforeUpload = (file) => {
      reportForm.value.rescuePhoto = file;
      return false; // 阻止默认上传行为
  };

  // 救助上报表单数据
  const reportForm = ref({
    shelterId: null,
    location: '',
    rescuePhoto: null,
    healthStatus: ''
  });
  // 捐赠表单数据
  const DonationForm = ref({
    amount: '',
    itemType: '',
  });
  // 申请救助站表单数据
  const applyForm = ref({
    shelterName: '',
    address: '',
    contactInfo: '',
  });
  // 表单验证规则 DonationForm
  const reportRules = ref({
    shelterId: [
      { required: true, message: '请输入救助站 ID', trigger: 'blur' }
    ],
    location: [
      { required: true, message: '请输入发现位置', trigger: 'blur' }
    ],
    rescuePhoto: [
      { required: true, trigger: 'blur' }
    ],
    healthStatus: [
      { required: true, message: '请输入健康状况', trigger: 'blur' }
    ]
  });
  const DonationFormRules = ref({
    amount: [
      { required: true, message: '请输入救助站 ID', trigger: 'blur' }
    ],
    itemType: [
      { required: true, message: '请输入发现位置', trigger: 'blur' }
    ],
  });
  // 表单验证规则
  const applyRules = ref({
    shelterName: [
      { required: true, message: '请输入救助站名称', trigger: 'blur' }
    ],
    address: [
      { required: true, message: '请输入救助站地址', trigger: 'blur' }
    ],
    contactInfo: [
      { required: true, message: '请输入救助站联系方式', trigger: 'blur' }
    ],
  });
  // 表单引用DonationForm
  const DonationFormRef = ref(null);
  const reportFormRef = ref(null);
  const applyFormRef = ref(null);
  const newsList = [
    {
        title: '我国每年新增超千万只流浪动物，仅有不足2%得到救助',
        content: '近日，一则流浪猫被射伤事件被报道后，流浪动物再次成为社会关注的焦点。《2023-2024年中国宠物行业白皮书（消费报...',
        url: 'https://www.sohu.com/a/866559079_100001695',
        date: '2025-03-04',
        source: '中国绿发会'
    },
    {
        title: '善待动物关爱生命，生生不息多方位守护流浪动物',
        content: '随着社会经济的发展和人们生活水平的不断提高，养宠风潮越来越盛行。与此同时，被丢弃、成为流浪动物的宠物数量不断增...',
        url: 'https://baijiahao.baidu.com/s?id=1816222693472218341',
        date: '2024-11-20',
        source: '新闻晨报'
    },
    {
        title: '流浪狗问题难以解决：数量激增，救助面临挑战',
        content: '流浪狗问题无法根除，但我们可以采取行动来改变现状。综合国际世卫组织及2021年中国宠物行业白皮书等多方权威数...',
        url: 'https://baijiahao.baidu.com/s?id=1824177606054439458',
        date: '2025-02-16',
        source: '百度新闻'
    }
  ]
  const Id=ref('')
  const truncateContent = (text, length) => {
    return text.length > length ? text.slice(0, length) + '...' : text
  }
  const handleReport = (id) => {
    // 处理救助上报逻辑
    console.log('点击了救助上报按钮');
    reportDialogVisible.value = true
    Id.value = id
  };
  const handleDonation = (id) => {
    // 处理救助上报逻辑
    console.log('点击了救助上报按钮');
    DonationDialogVisible.value = true
    Id.value = id
  };
  // 获取救助站数据
  const fetchShelters = async () => {
    try {
      const response = await service.getShelters()
      console.log(response)
      if (response.code === 200) {
        console.log('获取救助站数据成功:', response.data)
        Shelters.value = response.data
      } else {
        error.value = '数据加载失败：' + response.message
      }
    } catch (err) {
      error.value = '请求失败，请检查网络连接'
    } finally {
      loading.value = false
    }
  }
  // 添加搜索逻辑
  const handleSearch = async() => {
    // 执行搜索操作
    if(searchKeyword.value == ''){
        console.log("空空空空空空空空空空")
        ElMessage.error('关键词为空')
        fetchShelters()
        return ;
    }
    try {
        const response = await service.searchShelters(searchKeyword.value)
        console.log(response)
        if (response.code === 200) {
            console.log('获取申请记录成功:', response.data)
            ElMessage.success('搜索成功！')
            Shelters.value = response.data
        } else {
            console.error('获取申请记录失败:', response.message)
            ElMessage.error(response.message)
            Shelters.value = []
        }
    } catch (error) {
        console.error('请求异常:', error)
        throw error
    }
    console.log('当前搜索关键词：', searchKeyword.value)
  }
  const handleApply = async () => {
    // 处理救助上报逻辑
    console.log('点击了申请成为救助站');
    applyDialogVisible.value = true
    // console.log('当前搜索关键词')
  }
  // 过滤救助站
  const filteredShelters = computed(() => {
    return Shelters.value.filter(Shelters =>
      Shelters.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      Shelters.address.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  })
  // 处理提交事件
  const handleReportSubmit = async() => {
    try {
      if (!reportForm.value.rescuePhoto) {
          ElMessage.error('请选择照片');
          return;
      }
      const response = await service.addRescueReport(Id.value,reportForm.value,reportForm.value.rescuePhoto)
      if (response.code === 200) {
        ElMessage.success('上报成功');
        console.log('上报数据：', response.data);
        reportDialogVisible.value = false;
        uploadRef.value.clearFiles();
        reportForm.value = {
                shelterId: null,
                location: '',
                rescuePhoto: null,
                healthStatus: ''
            };
      }    
    } catch (error) {
      console.error('请求出错', error);
      alert('请求出错，请稍后重试');
    } 
  };
  const handleDonationSubmit = async() => {
    try {
      const response = await service.summitDonation(Id.value,DonationForm.value)
      if (response.code === 200) {
        ElMessage.success('捐赠成功');
        console.log('捐赠数据：', response.data);
        DonationDialogVisible.value = false;
      }    
    } catch (error) {
      console.error('请求出错', error);
      alert('请求出错，请稍后重试');
    } 
  };
  //申请成为救助站
  const handleapplySubmit = async() => {
    try {
      const response = await service.applyForShelter(applyForm.value)
      if (response.code === 200) {
        ElMessage.success('申请成功');
        console.log('申请数据：', response.data);
        applyDialogVisible.value = false;
      }    
    } catch (error) {
      console.error('请求出错', error);
      alert('请求出错，请稍后重试');
    } 
  };
  onMounted(() => {
    fetchShelters()
  })
</script>
  
<style scoped lang="scss">
    .shelter-container {
      height: 100vh;
      display: flex;
      flex-direction: column;
      // background: #f5f7fa;
    }

    .search-bar {
      padding: 20px;
      background: #fff;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
      display: flex;
      justify-content: center;
  
      .search-container {
        display: flex;
        gap: 10px;
        max-width: 800px;
        width: 100%;
      }
  
      .search-input {
        flex: 1;
  
        // border-radius: 13px;
        :deep(.el-input__inner) {
          border-radius: 8px 0 0 8px;
        }
      }
  
      .search-btn {
        margin-left: 10px;
        // margin-right: 50px;
        border-radius: 13px;
        padding: 0 30px;
        font-weight: 500;
        letter-spacing: 1px;
        transition: all 0.3s;
        background: linear-gradient(135deg, #409eff, #3375b9);
        border: none;
  
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
        }
  
        &:active {
          transform: translateY(1px);
        }
      }
  
      .apply-btn {
        margin-left: 50px;
        border-radius: 13px;
        padding: 0 30px;
        font-weight: 500;
        letter-spacing: 1px;
        transition: all 0.3s;
        background: linear-gradient(135deg, #67c23a, #4e9d2a);
        border: none;
  
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
        }
  
        &:active {
          transform: translateY(1px);
        }
      }
    }
  
    .main-content {
      flex: 1;
      display: flex;
      padding: 20px;
      gap: 20px;
      overflow: hidden; // 新增
    }
  
    .shelter-list {
      flex: 1;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
      overflow: hidden;
      height: 485px;
      max-width: 63%;
    
      .shelter-item {
        padding: 20px 0px 20px 70px;
        border-bottom: 1px solid #ebeef5;
        cursor: pointer;
        transition: all 0.3s;
        display: flex;
        /* 添加 flex 布局 */
        justify-content: space-between;
        /* 使内容左右分布 */
        align-items: center;
        /* 垂直居中对齐 */
    
        &:hover {
          background: #f5f7fa;
        }
    
        &.active {
          background: #e8f4ff;
          border-left: 4px solid #1a73e8;
        }
    
        .shelter-info-wrapper {
          flex: 1;
          /* 让救助站信息部分占据剩余空间 */
        }
    
        .shelter-name {
          color: #303133;
          margin-bottom: 10px;
        }
    
        .shelter-info {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #606266;
          margin-bottom: 8px;
        }
    
        .shelter-desc {
          color: #909399;
          font-size: 14px;
          line-height: 1.5;
        }
      }
    }
    .report-btn {
      margin-right: 30px;
      /* 调整按钮右侧边距 */
      border-radius: 13px;
      padding: 0 30px;
      font-weight: 500;
      letter-spacing: 1px;
      transition: all 0.3s;
      background: linear-gradient(135deg, #67c23a, #4e9d2a);
      border: none;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
      }

      &:active {
        transform: translateY(1px);
      }
    }
    .donation-btn {
      margin-right: 0px;
      /* 调整按钮右侧边距 */
      border-radius: 13px;
      padding: 0 30px;
      font-weight: 500;
      letter-spacing: 1px;
      transition: all 0.3s;
      background: linear-gradient(135deg, #409eff, #3375b9);
      border: none;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      }

      &:active {
        transform: translateY(1px);
      }
    }
    .news-sidebar {
      max-width: 400px;
      max-height: 500px;
      background: #fff;
      border-radius: 8px;
      // position: fixed; /* 新增属性，使导航栏固定 */
      // top: 50; /* 新增属性，将导航栏固定在顶部 */
      // right: 0;
      position: sticky;
      // overflow-y: auto;
      padding: 0px 20px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
      overflow: hidden;
  
      .news-item {
        padding: 16px 0;
        border-bottom: 1px solid #ebeef5;
  
        &:last-child {
          border-bottom: none;
        }
  
        .news-title {
          color: #1a73e8;
          font-weight: 500;
          text-decoration: none;
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          transition: color 0.3s;
  
          &:hover {
            color: #0056b3;
            text-decoration: underline;
          }
  
          .el-icon {
            margin-left: 6px;
            font-size: 14px;
          }
        }
  
        .news-content {
          color: #606266;
          font-size: 14px;
          line-height: 1.6;
          margin-bottom: 8px;
        }
  
        .read-more {
          color: #409eff;
          font-size: 12px;
          margin-left: 4px;
          text-decoration: none;
  
          &:hover {
            text-decoration: underline;
          }
        }
  
        .news-meta {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: #909399;
  
          .news-date {
            &::before {
              content: '📅 ';
            }
          }
  
          .news-source {
            &::before {
              content: '来源：';
            }
          }
        }
      }
    }
  
    .error-message {
      padding: 20px;
    }
  
    /* 流浪动物救助上报对话框样式 */
    ::v-deep .report-dialog {
      background: linear-gradient(180deg, #f7fffe, #ffffff);
      /* 对话框整体样式 */
      width: 600px;
      border-radius: 8px;
      .el-dialog__header {
        // background-color: #f0f9eb;
        border-bottom: 1px solid #e5e5e5;
      }

      .el-dialog__title {

        color: #67c23a;
        font-weight: 600;
      }

      .el-dialog__body {
        padding: 20px;
      }

      /* 表单样式 */
      .report-form {

        // gap: 20px;
        // padding-top: 20px;
        .el-form-item {
          margin-bottom: 30px;
        }

        .el-form-item__label {
          font-weight: 500;
        }
      }
      .photo-btn {
          margin-left: 10px;
          background-color: #67c23a;
          border: none;
          color: white;
          padding: 8px 16px;
          border-radius: 4px;
          transition: all 0.3s;
    
          &:hover {
            background-color: #5daf34;
          }
        }     
      /* 底部按钮样式 */
      .dialog-footer {
        display: flex;
        justify-content: center;
        gap: 10px;
        padding: 10px 20px;

        .cancel-btn {
          color: #909399;
          border: 1px solid #dcdfe6;
          padding: 8px 16px;
          border-radius: 4px;
          transition: all 0.3s;

          &:hover {
            color: #606266;
            border-color: #c0c4cc;
          }
        }

        .submit-btn {
          background-color: #67c23a;
          border: none;
          color: white;
          padding: 8px 16px;
          border-radius: 4px;
          transition: all 0.3s;

          &:hover {
            background-color: #5daf34;
          }
        }
      }
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