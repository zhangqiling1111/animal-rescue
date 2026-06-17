import axios from './../utils/axios'

// 登录
function login(Username,Pwd){
    return axios({
        url:'/user/login',
        method:'post',
        // headers: {
            
        //     'token': '23423d3dd'
        // },
        data:{
            username: Username,
            password: Pwd,
        }
    })
}
// 注册
function register(Username,Email,Pwd,Phone,Avatar){
    return axios({
        url:'/user/register',
        method:'post',
        headers: {
            'Content-Type': 'application/json'
        },
        data:{
            username: Username,
            email: Email,
            passwordHash: Pwd,          
            phone: Phone,
            avatarUrl: Avatar,
        }
    })
}
// 根据用户ID查询用户信息
function getUserInfoById(UserId) {
    return axios({
        url: `/admin/getUsersInfoById`,
        method: 'get',
        headers: {
            'Content-Type': 'application/json'
        },
        params:{
            userId:UserId
        }
    })
}
// 修改用户信息
function updateUserProfile(Email, Phone) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    
    return axios({
        url: '/user/profile',
        method: 'put',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        data: {
            email: Email,
            phone: Phone
        }
    })
}
//领养申请
function adopt(AnimalId,Reason){
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: `/adoption/apply/${AnimalId}`,
        method: 'post',
        headers: {           
            'token': token
        },
        data: {
            reason:Reason,
        }
    })
}
//获取流浪动物信息
function getAnimals(page,size) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/animals/getAnimals',
        method: 'get',
        headers: {
            
            'token': token
        },
        params:{
            page: page,
            limit:size,
        }
    })
}
//获取流浪动物总数
function getAnimalTotalNum() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/animals/getAnimalTotalNum',
        method: 'get',
        headers: {
            
            'token': token
        },
    })
}
//搜索流浪动物信息
function searchAnimals(data) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/animals/search',
        method: 'get',
        headers: {           
            'token': token
        },
        params:{
            breed: data.breed,
            area:data.area,
            healthStatus: data.healthStatus,
            isAdoptable:data.isAdoptable,
            shelterName: data.shelterName,
        }
    })
}
//获取所有救助站信息
function getShelters() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/shelters/all',
        method: 'get',
        headers: {           
            'token': token
        },       
    })
}
//搜索救助站
function searchShelters(keyword) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/shelters/shelters/search',
        method: 'get',
        headers: {           
            'token': token
        },  
        params:{
            shelterName:keyword,
            address:keyword,
        }     
    })
}
//修改个人信息
function updateProfile(data) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    const avatar = localStorage.getItem('avatar');
    return axios({
        url: '/user/profile',
        method: 'put',
        headers: {           
            'token': token
        },
        data:{
            email:data.email,
            phone:data.phone,
            avatarUrl:avatar,
        }
    })
}
//修改密码
function updatePassword(data) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/user/password',
        method: 'put',
        headers: {           
            'token': token
        },
        data:{
            currentPassword:data.oldPassword,
            newPassword:data.newPassword,
        }
    })
}
//获取所有领养申请记录
function getAdoptionDetails() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/adoption/details',
        method: 'get',
        headers: {           
            'token': token
        },
    })
}
//根据品种获取领养申请记录
function searchAdoptionApplicationsByBreed(Breed) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/adoption/searchByBreed',
        method: 'get',
        headers: {           
            'token': token
        },
        params:{
            breed:Breed,
        }
    })
}
//取消申请
function cancelAdoption(applicationId){
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: `/adoption/cancel/${applicationId}`,
        method: 'delete',
        headers: {           
            'token': token
        },
    })
}
//申请成为救助站
function applyForShelter(data) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/shelters/apply',
        method: 'post',
        headers: {           
            'token': token
        },
        data:{
            shelterName:data.shelterName,
            address:data.address,
            contactInfo:data.contactInfo,
        },
    })
}
//取消申请
function cancelShelterApplication(applicationId){
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: `/shelters/cancel/${applicationId}`,
        method: 'delete',
        headers: {           
            'token': token
        },
    })
}
//获取所有救助站申请记录
function getShelterApplicationDetails() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/shelters/application/details',
        method: 'get',
        headers: {           
            'token': token
        },
    })
}
//救助上报
function addRescueReport(id,data,file) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    const formData = new FormData();
    formData.append('token', token);
    formData.append('shelterId', id);
    formData.append('location', data.location);
    formData.append('healthStatus', data.healthStatus);
    formData.append('rescuePhoto', file);
    return axios({
        url: '/rescue/summitReport',
        method: 'post',
        headers: {           
            'token': token,
            'Content-Type': 'multipart/form-data'
        },
        data: formData,
    })
}
//获取所有救助上报记录
function getReportByUserId() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/rescue/getReportByUserId',
        method: 'get',
        headers: {           
            'token': token
        },
    })
}
//捐赠功能·
function summitDonation(id,data) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/donate/summitDonation',
        method: 'post',
        headers: {           
            'token': token
        },
        data:{
            shelterId:id,
            amount:data.amount,
            itemType:data.itemType,
        },
    })
}
//获取所有捐赠记录
function getPersonalRecords() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/donate/getPersonalRecords',
        method: 'get',
        headers: {           
            'token': token
        },
    })
}
export default {
    login,
    register,
    getUserInfoById,
    updateUserProfile,
    getAnimals,
    adopt,
    searchAnimals,
    getShelters,
    updateProfile,
    updatePassword,
    getAdoptionDetails,
    addRescueReport,
    cancelAdoption,
    applyForShelter,
    getShelterApplicationDetails,
    cancelShelterApplication,
    getReportByUserId,
    summitDonation,
    getPersonalRecords,
    searchAdoptionApplicationsByBreed,
    searchShelters,
    getAnimalTotalNum,
}