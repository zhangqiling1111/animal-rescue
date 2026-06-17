<template>
  <div class="background-container">
    <div class="login-registration-wrapper">
      <el-form class="login_form" :model="formlogin" label-width="auto" style="max-width: 600px">
        <div class="header-wrapper">
          <div class="header">
            <h1 class="title">流浪动物救助系统</h1>
          </div>
        </div>
        <el-form-item label="用户名">
          <el-input v-model="formlogin.username" />
        </el-form-item>
        <el-form-item class="pass" label="密码">
          <el-input v-model="formlogin.pwd" type="password" />
        </el-form-item>

        <el-form-item>
          <el-button class="login" type="primary" @click="onSubmit">登录</el-button>
          <el-button class="forget" @click="showdialog()">注册</el-button>
        </el-form-item>
      </el-form>
      <!-- 注册对话框 -->
      <el-dialog
        class="regdialog"
        title="注册"
        v-model="dialogVisible"
        width="30%"
      >
        <el-form :model="form" :rules="rules" ref="formRef" class="regdialog">
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="form.password"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="form.confirmPassword"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input  v-model="form.email"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input  v-model="form.phone"></el-input>
          </el-form-item>
          <span slot="footer" class="dialog-footer">
            <el-button class="regfalse" @click="dialogVisible = false">取消</el-button>
            <el-button class="reg" type="primary" @click="handleRegister">注册</el-button>
          </span>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import service from '../../service/user.js'
// import axios from 'axios';
const router = useRouter()
// 获取表单引用
const formRef = ref<InstanceType<typeof import('element-plus').ElForm>>(null);
 
 // 控制对话框的显示与隐藏
const dialogVisible = ref(false);
// 定义表单数据
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
});
 
const userdata=ref(null)
const formlogin = ref({
  username: '',
  pwd: '',
});
function showdialog(){
  dialogVisible.value = true;
}
// 定义表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  // password: [
  //   { required: true, message: '请输入密码', trigger: 'blur' },
  //   { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' },
  // ],
  // confirmPassword: [
  //   { required: true, message: '请确认密码', trigger: 'blur' },
  //   {
  //     validator: (rule, value, callback) => {
  //       if (value !== form.value.password) {
  //         callback(new Error('两次输入密码不一致'));
  //       } else {
  //         callback();
  //       }
  //     },
  //     trigger: 'blur',
  //   },
  // ],
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
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await service.register(
          form.value.username,
          form.value.email,
          form.value.password,         
          form.value.phone,
          'https://iconfont.alicdn.com/p/illus/preview_image/vKhn39joZS7d/b3bb28bd-1cf2-4869-8701-985093ebe0ff.png')
        ElMessage.success('注册成功');
        dialogVisible.value = false;
      } catch (error) {
        console.error('请求出错', error);
        ElMessage.error('注册失败')
      }
    } else {
      console.log('表单验证失败');
    }
  });
};
 
async function onSubmit(){
  try {
    const response = await service.login(formlogin.value.username,formlogin.value.pwd)
    const token = response.data.token;
    console.log(token)   
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
        router.push('/home/shelter');
        // router.push('/...../......');加上你的跳转       
      }else if(response.data.user.role == "ADMIN"){
        console.log('管理员登录成功');
        router.push('/admin/AdminCenter');
      }
      
    } else {
      ElMessage.error('登录失败')
      console.error('登录失败', response.data.message);
    }
  } catch (error) {
    console.error('请求出错', error);
  }
}

</script>

<style scoped lang="scss">
.background-container {
  background-image: url('../../assets/背景10.webp');
  /* 替换为你的背景图 URL */
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: 0;
}

:deep(.el-dialog) {
  border-radius: 8px;
}
.login-registration-wrapper {
  background-color: rgba(255, 255, 255, 0.7); /* 设置一定的透明度 */
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.regdialog {
  padding: 10px;
}
:deep(.el-form-item__label) {
  color: #000000; /* 示例颜色（Element Plus 主题色），可替换为任意颜色值 */
  font-weight: 530; /* 可选：调整字体粗细 */
  font-size: 1.1em;
}
.dialog-footer {
  // text-align: center;
  padding-left: 90px;
}

.login_form {
  margin: 0 auto;
  // margin-top: 130px;
  .header-wrapper {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    text-align: center;
    padding-top: 0px;
    margin-bottom: 20px;
  }

  .header {
    padding: 20px;
    max-width: 600px; /* 限制最大宽度，但允许在小屏幕上缩小 */
    width: 100%; /* 在大屏幕上占据全部宽度 */
    box-sizing: border-box; /* 确保内边距和边框包含在宽度内 */
    margin-left: 0px;
  }

  .title {
    margin: 0;
    font-size: 2.0em; /* 增大字体，但可能需要在大屏幕上调整 */
    color: #222222; /* 深灰色文字 */
  }
  
  .pass {
    margin-top: 40px;
  }

  .login {
    background-color: #409eff;
    border-color: #409eff;
    margin-top: 30px;
    margin-left: 80px;
    &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      }
  }
  .forget {
    margin-left: 10px;
    margin-top: 30px;
  }
}

.reg {
  background-color: #409eff;
  color: #fff;
  // margin-left: 100px;
  // padding-left: 30px;
  border-color: #409eff;
}

.reg:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.regfalse {
  margin-right: 25px;
}
</style>    