<template>
  <div class="background-container">
    <div class="login-registration-wrapper">
      <!-- 登录状态面板（初始显示） -->
      <div 
        v-if="isLoginState" 
        class="slide-container login-state" 
        :style="loginStateStyle"
      >
        <!-- 登录区域（左2/3） -->
        <div class="login-panel">
          <el-form class="login_form" :model="formlogin" label-width="auto" ref="formRef" >
            <div class="header-wrapper">
              <div class="header">
                <h1 class="title">流浪动物救助系统</h1>
              </div>
            </div>
            <el-form-item >
              <el-input v-model="formlogin.username" placeholder="账号"/>
            </el-form-item>
            <el-form-item >
              <el-input v-model="formlogin.pwd" type="password" placeholder="密码"/>
            </el-form-item>
            <el-form-item>
              <el-button class="login" type="primary" @click="onSubmit">登录</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 引导注册区域（右1/3） -->
        <div class="guide-panel1">
          <h>您好，朋友</h>
          <p>欢迎来到流浪动物救助系统</p>
          <el-button class="switch-btn" @click="toggleSlide">去注册</el-button>
        </div>
      </div>

      <!-- 注册状态面板（滑动后显示） -->
      <div 
        v-else 
        class="slide-container registration-state" 
        :style="registrationStateStyle"
      >
        <!-- 引导登录区域（左1/3） -->
        <div class="guide-panel2">
          <h>欢迎回来！</h>
          <p>去登录进入流浪动物救助系统吧</p>
          <el-button class="switch-btn" @click="toggleSlide">去登录</el-button>
        </div>

        <!-- 注册区域（右2/3） -->
        <div class="registration-panel">
          <el-form :model="form"  ref="formRef" class="regdialog">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="账号"></el-input>
            </el-form-item>
            <el-form-item  prop="password">
              <el-input type="password" v-model="form.password" placeholder="密码"></el-input>
            </el-form-item>
            <el-form-item  prop="confirmPassword" >
              <el-input type="password" v-model="form.confirmPassword" placeholder="确认密码"></el-input>
            </el-form-item>
            <el-form-item  prop="email">
              <el-input v-model="form.email" placeholder="邮箱"></el-input>
            </el-form-item>
            <el-form-item  prop="phone">
              <el-input v-model="form.phone" placeholder="联系方式"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button class="reg" type="primary" @click="handleRegister">注册</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../service/user.js'
// import axios from 'axios';
const router = useRouter()
// 获取表单引用
const formRef = ref<InstanceType<typeof import('element-plus').ElForm>>(null);
// 状态控制
const isLoginState = ref(true); // 初始为登录状态
const formlogin = ref({ username: '', pwd: '' });
const form = ref({ username: '', password: '', confirmPassword: '', email: '', phone: '' });
// 定义表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.value.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ]
};
// 处理注册逻辑
const handleRegister = async () => { 
  try {
    const response = await service.register(
      form.value.username,
      form.value.email,
      form.value.password,
      form.value.phone,
      'https://iconfont.alicdn.com/p/illus/preview_image/vKhn39joZS7d/b3bb28bd-1cf2-4869-8701-985093ebe0ff.png')
    if(response.code === 200){
      ElMessage.success('注册成功');
      toggleSlide()
    } else {
      ElMessage.error(response.message)
      console.error('登录失败', response.message);
    }
    
  } catch (error) {
    console.error('请求出错', error);
    ElMessage.error('注册失败')
  }
};
 
async function onSubmit(){
  try {
    const response = await service.login(formlogin.value.username,formlogin.value.pwd)
       
    if ( response.code === 200) {
      ElMessage.success('登录成功');
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('success', response.data.success);
      // 存储用户信息到 localStorage
      localStorage.setItem('userId', response.data.user.userId);
      localStorage.setItem('email', response.data.user.email);
      localStorage.setItem('phone', response.data.user.phone);
      localStorage.setItem('avatar', response.data.user.avatarUrl);
      localStorage.setItem('username', response.data.user.username); // 可选存储用户名
      localStorage.setItem('role', response.data.user.role); // 可选存储角色
      if(response.data.user.role == "USER"){       
        router.push('/home/adoptioncenter');
      }else if(response.data.user.role == "SHELTER"){
        localStorage.setItem('shelterId', response.data.shelterId.shelterId);//救助站id
        localStorage.setItem('address', response.data.shelterId.address);
        localStorage.setItem('contactInfo', response.data.shelterId.contactInfo);
        localStorage.setItem('name', response.data.shelterId.name);
        console.log('救助站登录成功');
        router.push('/home/shelter/animals');
        // router.push('/...../......');加上你的跳转       
      }else if(response.data.user.role == "ADMIN"){
        console.log('管理员登录成功');
        router.push('/admin/AdminCenter');
      }
    } else {
      ElMessage.error(response.message)
      console.error('登录失败', response.message);
    }
  } catch (error) {
    console.error('请求出错', error);
  }
}
// 滑动动画样式
const loginStateStyle = ref({
  transform: 'translateX(0)',
  opacity: 1
});

const registrationStateStyle = ref({
  transform: 'translateX(100%)',
  opacity: 0
});

const toggleSlide = () => {
  if (isLoginState.value) {
    // 从登录状态切换到注册状态
    loginStateStyle.value = {
      transform: 'translateX(-100%)',
      opacity: 0,
      transition: 'all 0.5s ease-out'
    };
    registrationStateStyle.value = {
      transform: 'translateX(0)',
      opacity: 1,
      transition: 'all 0.5s ease-in'
    };
  } else {
    // 从注册状态切换到登录状态
    loginStateStyle.value = {
      transform: 'translateX(0)',
      opacity: 1,
      transition: 'all 0.5s ease-in'
    };
    registrationStateStyle.value = {
      transform: 'translateX(100%)',
      opacity: 0,
      transition: 'all 0.5s ease-out'
    };
  }
  // 切换状态（延迟确保动画完成后更新DOM）
  setTimeout(() => {
    isLoginState.value = !isLoginState.value;
  }, 500);
};
</script>

<style scoped lang="scss">
/* 设置全局样式 */
body {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
}

.background-container {
  // background: linear-gradient(135deg, #667eea, #764ba2);
  background-image: url('../../assets/背景8.png');
  background-size: cover;
  background-position: center;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-registration-wrapper {
  position: relative;
  width: 700px;
  height: 400px;
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 5px 5px 20px rgba(0, 0, 0, 0.5);
}

.slide-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  transition: all 0.5s ease;
}

.login-state, .registration-state {
  display: flex;
  width: 100%;
  height: 100%;
}

.login-panel {
  flex: 3;
  padding: 30px 0px;
  background-color: #ffffff;
  // background-color: rgba(255, 255, 255, 0.92);
  // background: linear-gradient(225deg, #aadcff, #ffffff);
  border-radius: 10px 0 0 10px;
}

.registration-panel {
  // background-color: #ffffff;
  flex: 3;
  padding: 30px 0px;
  background: linear-gradient(90deg, #99d5ff, #ffffff);
  // opacity: 0.97;
  border-radius: 0 10px 10px 0;
}

.guide-panel1 {
  flex: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  background: linear-gradient(90deg, #1b76e5, #88efff);
  // border-left: 1px solid #e0e0e0;
}
.guide-panel2 {
  flex: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  background: linear-gradient(270deg, #1b76e5, #88efff);
  // border-left: 1px solid #e0e0e0;
}

.switch-btn {
  margin-top: 20px;
  padding: 10px 25px;
  border-radius: 25px;
  border: 2px solid #fbfcfc;
  color: #ffffff;
  background: transparent;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: #ddeeff;
    color: white;
    transform: scale(1.05);
  }
}

/* 原有表单样式保留 */
:deep(.login_form){
  width: 100%;
  max-width: 370px;
  margin: 0 auto;

  .title {
    font-size: 24px;
    color: #409eff;
    margin-bottom: 60px;
    text-align: center;
  }
  .login {
    width: 100%;
    max-width: 200px;
    margin: 30px auto 0;
  }
}
:deep(.regdialog){
  width: 100%;
  max-width: 370px;
  margin: 0 auto;
  .reg {
    width: 100%;
    max-width: 200px;
    margin: 5px auto 0;
  }
}
.login-panel .el-form-item {
  margin-bottom: 30px;
}
.registration-panel .el-form-item {
  margin-bottom: 20px;
}
/* 按钮样式优化 */
.el-button {
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
/* 美化 label */
:deep(.login-panel .el-form-item__label){
  min-width: 80px;
  height: 40px;
  font-size: 18px;
  color: #4da6ff;
  // font-weight: 550;
  // margin-bottom: 5px;
  margin-left: 0px;
  display: block;
  text-align: center; /* 让文字右对齐，配合固定宽度实现对齐效果 */
  padding-right: 5px; /* 适当添加内边距，让文字与输入框有一定间隔 */
  padding-top: 2px;
}
:deep(.registration-panel .el-form-item__label) {
  width: 90px;
  font-size: 18px;
  color: #53a9ff;
  font-weight: 550;
  // margin-bottom: 5px;
  display: block;
  text-align: center; /* 让文字右对齐，配合固定宽度实现对齐效果 */
  padding-right: 5px; /* 适当添加内边距，让文字与输入框有一定间隔 */
  padding-top: 2px;
}
/* 美化输入框 */
:deep(.login-panel .el-input){
  width: 300px;
  margin: 0 auto;
  height: 40px;
  border-radius: 13px;
  // border: 1px solid #ccc;
  // padding: 0 12px;
  font-size: 17px;
  transition: border-color 0.3s ease;

  &:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
}
:deep(.login-panel .el-input__wrapper){
  background-color: #eeeeee;
  // border-radius: 13px;
  // padding: 0 12px;
  padding-left: 20px;
}
:deep(.login-panel .el-input__inner::placeholder),
:deep(.registration-panel .el-input__inner::placeholder){
  color: #8c8c8c;
  font-size: 15px;
  letter-spacing: 0.5px;
  /* 以下为可选样式 */
  // text-align: center; //居中对齐 
  /* text-transform: uppercase; 字母大写 */
}
:deep(.registration-panel .el-input) {
  max-width: 340px;
  height: 40px;
  border-radius: 12px;
  margin: 0 auto;
  // border: 1px solid #ccc;
  padding: 0 12px;
  font-size: 17px;
  transition: border-color 0.3s ease;

  &:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
}
:deep(.registration-panel .el-input__wrapper){
  background-color: #f2f8fd;
  border-radius: 12px;
  // padding: 0 12px;
  padding-left: 20px;
}
/* 美化引导面板文字 */
.guide-panel1 p {
  font-size: 15px;
  color: #fff;
  margin-bottom: 5px;
}
.guide-panel1 h {
  font-weight: 550;
  font-size: 30px;
  color: #fff;
  margin-bottom: 5px;
}
.guide-panel2 p {
  font-size: 15px;
  color: #fff;
  margin-bottom: 5px;
}
.guide-panel2 h {
  font-weight: 550;
  font-size: 30px;
  color: #fff;
  margin-bottom: 5px;
}
/* 美化注册和登录按钮 */
.login,
.reg {
  background: linear-gradient(135deg, #409eff, #66b2ff);
  border: none;
  border-radius: 20px;
  height: 40px;
  font-size: 16px;
  font-weight: 600;
}

.login:hover,
.reg:hover {
  background: linear-gradient(135deg, #3385ff, #599dff);
}
</style>
    