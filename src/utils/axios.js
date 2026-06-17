import axios from 'axios'

// 配置请求的根路径
const ConfigBaseURL = 'http://localhost:28080';

// 让ajax携带cookie(自动携带本地所有cookies)
// axios.defaults.withCredentials=true;

// 使用create方法创建axios实例
const Service = axios.create({
  baseURL: ConfigBaseURL,
  //timeout: 7000
});

// 给POST请求添加请求头设置（不同项目，值不一样）
Service.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
Service.defaults.headers.get['Content-Type'] = 'application/json;charset=UTF-8';
// 添加请求拦截器
Service.interceptors.request.use(config => {
  return config;
});

//4.2 添加响应拦截器
Service.interceptors.response.use(response => {
    console.log("axios 的响应拦截",response.data.code);
    if (response.data.code == 0) {
      return Promise.reject(response.data.msg);
    }
    console.log(response.data)
    return response.data;

  }, error => {
    return Promise.reject(error);
  });
  
  export default Service;