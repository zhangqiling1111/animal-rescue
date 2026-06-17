import axios from './../utils/axios'
//获取救助站申请记录
function getAllApplications() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/admin/shelterApplications',
        method: 'get',
        headers: {           
            'token': token
        },
    })
}
//通过救助站申请
function approveApplication(applicationId) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: `/admin/approveApplication/${applicationId}`,
        method: 'post',
        headers: {           
            'token': token
        },
    })
}
//拒绝救助站申请
function rejectApplication(applicationId) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: `/admin/rejectApplication/${applicationId}`,
        method: 'post',
        headers: {           
            'token': token
        },
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
        url: '/shelters/search',
        method: 'get',
        headers: {           
            'token': token
        },  
        params:{
            shelterName:keyword,
            // address:keyword,
        }     
    })
}
//获取所有用户信息
function getUsersInfo() {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: '/user/getUsersInfo',
        method: 'get',
        headers: {           
            'token': token
        },       
    })
}
//冻结用户
function freezeUser(userId) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: `/admin/freezeUser/${userId}`,
        method: 'post',
        headers: {           
            'token': token
        },
    })
}
//解冻用户
function unfreezeUser(userId) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log(token)
    return axios({
        url: `/admin/unfreezeUser/${userId}`,
        method: 'post',
        headers: {           
            'token': token
        },
    })
}
export default {
    getAllApplications,
    approveApplication,
    rejectApplication,
    getShelters,
    getUsersInfo,
    freezeUser,
    unfreezeUser,
    searchShelters,
}