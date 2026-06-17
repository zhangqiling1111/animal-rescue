import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/loginviews/login-registration.vue'
import PersonalCenter from '../views/userinfo/PersonalCenter.vue'
import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs'

const routes = [
  {
    path: '',
    component: Login
  },
  {
    path:'/admin',
    name:'admin',
    component: () => import('../views/AdminHome.vue'),
    children:[
      {
        path:'AdminCenter',
        name:'AdminCenter',
        component: () => import('../views/Admin/AdminCenter.vue'),
        children:[
          {
            path: '',
            name: 'admininfo',
            component: () => import('../views/Admin/AdminInfo.vue')
          },
          {
            path: 'RescuestationApply',
            name: 'RescuestationApply',
            component: () => import('../views/Admin/RescuestationApply.vue')
          },
          {
            path: 'Rescuestation',
            name: 'Rescuestation',
            component: () => import('../views/Admin/Rescuestation.vue')
          },
          {
            path: 'UserManger',
            name: 'UserManger',
            component: () => import('../views/Admin/UserManger.vue')
          },
        ]
      },  
    ]
  },
  {
    path:'/home',
    name:'home',
    component: () => import('../views/UserHome.vue'),
    children:[
      {
        path:'adoptioncenter',
        name:'adoptioncenter',
        component: () => import('../views/Adoption/AdoptionCenter.vue')
      },      
      {
        path:'adoptmanger',
        name:'adoptmanger',
        component: () => import('../views/userinfo/AdoptionManger.vue')
      },
      {
        path:'rescuestation',
        name:'rescuestation',
        component: () => import('../views/RescueStation/rescuestation.vue')
      },
      {
        path:'PersonalCenter',
        name:'PersonalCenter',
        component: PersonalCenter,       
        children:[
          {
            path: '',
            name: 'info',
            component: () => import('../views/userinfo/Info.vue')
          },
          {
            path: 'Rescue',
            name: 'Rescue',
            component: () => import('../views/userinfo/RescuestationManger.vue')
          },
          {
            path: 'adopt',
            name: 'adopt',
            component: () => import('../views/userinfo/AdoptionManger.vue')
          },
          {
            path: 'report',
            name: 'report',
            component: () => import('../views/userinfo/ReportManger.vue')
          },
          {
            path: 'Donation',
            name: 'Donation',
            component: () => import('../views/userinfo/DonationManger.vue')
          },
        ]
      },
    ]
  },
  {
    path: '/home/shelter',
    name: 'shelter',
    component: () => import('../views/Shelter/ShelterHome.vue'), // 指向 Shelter 文件夹中的主组件
    children: [ 
      {
        path: 'animals',
        name: 'shelterAnimals',
        component: () => import('../views/Shelter/AnimalList.vue'), // 展示救助站动物信息列表
      },
      {
        path: 'add-animal',
        name: 'addAnimal',
        component: () => import('../views/Shelter/AddAnimal.vue'), // 添加动物信息界面
      },
      {
        path: 'edit-animal/:id',
        name: 'editAnimal',
        component: () => import('../views/Shelter/EditAnimal.vue'), // 修改动物信息界面
        props: true,
      },
      {
        path: 'rescue-records',
        name: 'rescueRecords',
        component: () => import('../views/Shelter/RescueRecords.vue'), // 查看救助记录界面
      },
      {
        path: 'donation-records',
        name: 'donationRecords',
        component: () => import('../views/Shelter/DonationRecords.vue'), // 查询捐赠记录界面
      },
      {
        path: 'personal-center',
        name: 'personalCenter',
        component: () => import('../views/Shelter/PersonalCenter.vue'), // 个人中心界面
        meta: { title: '个人中心' }
      },
      {
        path: 'adoption-applications',
        name: 'adoptionApplications',
        component: () => import('../views/Shelter/AdoptionApplications.vue'),
        meta: { title: '领养申请审核' }
      },
      {
        path: 'rescue-management',
        name: 'rescueManagement',
        component: () => import('../views/Shelter/RescueManagement.vue'),
        meta: { title: '救助管理' }
      }
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router