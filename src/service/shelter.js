//const API_BASE = 'https://www.kdocs.cn/l/cvEqBfc8zBGz';
import axios from './../utils/axios';

export default {
  // 查询救助站的所有动物信息
  getAnimals() {
    const token = localStorage.getItem('token');
    console.log('获取动物列表使用的token:', token);
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    return axios({
      url: '/animals/getAnimalsInShelter',
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      params: {
        page: 1,
        limit: 100
      }
    });
  },
  // 添加动物信息 (恢复为发送 JSON, 不含图片)
  addAnimal(data) { // 接收包含动物信息的普通对象
    const token = localStorage.getItem('token');
    console.log('添加动物(JSON)使用的token:', token);
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    const shelterId = localStorage.getItem('shelterId');
    if (!shelterId) {
      return Promise.reject(new Error('未找到救助站ID，请重新登录'));
    }
    
    // 准备要发送的 JSON 数据 (不含 animalPhoto)
    const jsonData = {
        breed: data.breed,
        age: parseInt(data.age, 10),
        healthStatus: data.healthStatus, // 使用 API 文档中的字段名
        isAdoptable: data.isAdoptable ? 1 : 0 // 使用 API 文档中的字段名
    };
    
    console.log('发送添加动物请求 (JSON)，救助站ID:', shelterId, '数据:', jsonData);

    return axios({
      url: `/animals/addAnimal`, 
      method: 'post',
      headers: {
        'Content-Type': 'application/json', // 明确发送 JSON
        'token': token
      },
      params: { // shelterId 作为查询参数
        shelterId: parseInt(shelterId, 10)
      },
      data: jsonData // 发送 JSON 数据
    });
  },
  // 修改动物信息
  updateAnimal(animalId, data) {
    const token = localStorage.getItem('token');
    console.log('修改动物使用的token:', token);
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    // 确保 animalId 是数字
    const id = parseInt(animalId, 10);
    if (isNaN(id)) {
      return Promise.reject(new Error('无效的动物ID'));
    }
    
    console.log('发送的更新数据:', data);
    
    return axios({
      url: '/animals/updateAnimalInfo',
      method: 'put',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      data: {
        animalId: id,
        breed: data.breed,
        age: data.age,
        healthStatus: data.healthStatus,
        isAdoptable: data.isAdoptable
      }
    });
  },
  // 查询救助记录
  getRescueRecords(params) {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    console.log('救助记录使用的token:', token); // 打印token以便调试
    console.log('查询救助记录参数:', params); // 打印传入的参数
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    return axios({
      url: `/rescue/getRescueRecords`,
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      params: params // <--- 修改这里，直接使用传入的 params 对象
    });
  },
  // 查询捐赠记录
    getDonationRecords() {
      const token = localStorage.getItem('token'); // 从本地存储获取token
      console.log('捐赠记录使用的token:', token); // 打印token以便调试
      
      if (!token) {
        return Promise.reject(new Error('未登录，请先登录'));
      }
  
      // 从本地存储获取 shelterId
      const shelterId = localStorage.getItem('shelterId');
      if (!shelterId) {
        return Promise.reject(new Error('未找到救助站ID，请重新登录'));
      }
      
      return axios({
        url: `/donate/getRecordsInShelter`,
        method: 'get',
        headers: {
          'Content-Type': 'application/json',
          'token': token
        },
        params: {
          shelterId: parseInt(shelterId, 10), // 确保 shelterId 是数字
          page: 1,
          limit: 10000,
          status: 'all'
        }
      });
    },
  // 删除动物信息
  deleteAnimal(animalId) {
    const token = localStorage.getItem('token');
    console.log('删除动物使用的token:', token);
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    // 确保 animalId 是数字
    const id = parseInt(animalId, 10);
    if (isNaN(id)) {
      return Promise.reject(new Error('无效的动物ID'));
    }
    
    console.log('发送删除请求，animalId:', id);
    console.log('请求URL:', '/animals/deleteAnimal');
    
    return axios({
      url: '/animals/deleteAnimal',
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      params: {
        animalId: id
      }
    });
  },
  // 获取个人信息
    getShelterInfo() {
      const token = localStorage.getItem('token');
      console.log('获取个人信息使用的token:', token);
      
      if (!token) {
        return Promise.reject(new Error('未登录，请先登录'));
      }
  
      // 从本地存储获取 shelterId
      const shelterId = localStorage.getItem('shelterId');
      if (!shelterId) {
        return Promise.reject(new Error('未找到救助站ID，请重新登录'));
      }
  
      console.log('从本地存储获取的shelterId:', shelterId);
  
      // 使用 shelterId 获取救助站信息
      return axios({
        url: `/shelters/${parseInt(shelterId, 10)}`, // 确保 shelterId 是数字
        method: 'get',
        headers: {
          'Content-Type': 'application/json',
          'token': token
        }
      });
    },
  // 修改密码
  changePassword(oldPassword, newPassword) {
    const token = localStorage.getItem('token');
    console.log('修改密码使用的token:', token);
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    return axios({
      url: '/user/password',
      method: 'put',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      data: {
        currentPassword: oldPassword,
        newPassword
      }
    });
  },
  // 修改救助站信息
      updateShelterInfo(data) {
      const token = localStorage.getItem('token');
      console.log('修改救助站信息使用的token:', token);
      
      if (!token) {
        return Promise.reject(new Error('未登录，请先登录'));
      }
    
      // 从本地存储获取 shelterId
      const shelterId = localStorage.getItem('shelterId');
      if (!shelterId) {
        return Promise.reject(new Error('未找到救助站ID，请重新登录'));
      }
    
      // 检查是否包含必填字段
      if (!data.shelterName) {
        return Promise.reject(new Error('缺少 shelterName 参数'));
      }
      if (!data.address) {
        return Promise.reject(new Error('缺少 address 参数'));
      }
      if (!data.contactInfo) {
        return Promise.reject(new Error('缺少 contactInfo 参数'));
      }
    
      return axios({
        url: '/shelters/update',
        method: 'put',
        headers: {
          'Content-Type': 'application/json',
          'token': token
        },
        params: { // 将 shelterName、address 和 contactInfo 作为查询参数传递
          shelterName: data.shelterName,
          address: data.address,
          contactInfo: data.contactInfo
        },
        data: {
          shelterId: parseInt(shelterId, 10), // 确保 shelterId 是数字
          phone: data.phone,
          email: data.email
        }
      });
    },
  // 获取救助上报列表
  getRescueReports(params) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    const shelterId = localStorage.getItem('shelterId');
    if (!shelterId) {
      return Promise.reject(new Error('未找到救助站ID，请重新登录'));
    }

    console.log('获取救助列表，shelterId:', shelterId);
    console.log('分页参数:', params);

    return axios({
      url: '/rescue/getReportByShelterId',
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      params: {
        shelterId: parseInt(shelterId, 10),
        page: params.page || 1,
        limit: params.limit || 10
      }
    });
  },
  
  // 获取救助详情
  getRescueDetails(rescueId) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    console.log(`获取救助详情: ID=${rescueId}`);

    return axios({
      url: `/rescue/getReportDetail/${rescueId}`,
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      }
    });
  },
  
  // 更新救助状态
  updateRescueStatus(rescueId, status, reason) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    const shelterId = localStorage.getItem('shelterId');
    if (!shelterId) {
      return Promise.reject(new Error('未找到救助站ID，请重新登录'));
    }

    const apiStatusMap = {
      'ACCEPTED': '已接收',
      'REJECTED': '已拒绝'
    };
    const apiStatus = apiStatusMap[status] || status;

    // 构造查询参数对象
    const queryParams = {
      shelterId: parseInt(shelterId, 10),
      reportId: parseInt(rescueId, 10),
      status: apiStatus,
      checkReason: reason || '' // 包含 checkReason，如果后端需要
    };

    console.log(`更新救助状态，发送参数 (Query Params):`, queryParams);

    return axios({
      url: '/rescue/checkReport',
      method: 'put', // 保持 PUT 方法
      headers: {
        // Content-Type 对于没有请求体的 PUT 通常不是必需的
        // 'Content-Type': 'application/json',
        'token': token
      },
      params: queryParams, // 将所有参数作为查询参数发送
      // 移除 data 属性
      // data: requestData 
    });
  },
  
  // 获取领养申请列表
  async getAdoptionApplications(params) {
    try {
      const shelterId = localStorage.getItem('shelterId');
      const token = localStorage.getItem('token');
      
      if (!shelterId) {
        console.error('获取领养申请列表失败: 未找到救助站ID');
        return { data: [], status: 400 };
      }
      
      // 添加时间戳防止缓存
      const timestamp = new Date().getTime();
      
      const requestPath = `/adoption/shelter/${shelterId}`;
      console.log('请求路径:', requestPath);
      console.log('请求方法: GET');
      console.log('请求参数:', { ...params, _t: timestamp });
      console.log('请求头 (使用 token):', {
        'token': token ? `${token.substring(0, 15)}...` : '无',
      });
      
      const response = await axios.get(requestPath, { 
        params: { ...params, _t: timestamp },
        headers: {
          'token': token
        }
      });
      
      console.log('API响应状态码:', response.status);
      console.log('API响应头:', response.headers);
      console.log('API原始响应数据:', response.data);
      
      // 详细分析响应数据结构
      const responseData = response.data;
      if (responseData) {
        if (responseData.records && Array.isArray(responseData.records)) {
          console.log('数据结构包含records数组，长度:', responseData.records.length);
          console.log('数据结构包含total字段:', responseData.total);
        } else if (responseData.data && Array.isArray(responseData.data)) {
          console.log('数据结构包含data数组，长度:', responseData.data.length);
          console.log('数据结构包含total字段:', responseData.total);
        } else if (Array.isArray(responseData)) {
          console.log('响应直接是数组，长度:', responseData.length);
        } else {
          console.log('响应是对象，检查是否包含数组字段:');
          for (const key in responseData) {
            if (Array.isArray(responseData[key])) {
              console.log(`- 字段 ${key} 是数组，长度:`, responseData[key].length);
            } else if (typeof responseData[key] === 'object' && responseData[key] !== null) {
              console.log(`- 字段 ${key} 是对象`);
            } else {
              console.log(`- 字段 ${key} 是基本类型:`, responseData[key]);
            }
          }
        }
      } else {
        console.log('API响应数据为空');
      }
      
      return response;
    } catch (error) {
      console.error('获取领养申请列表请求出错:', error.message);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误响应头:', error.response.headers);
        console.error('错误响应数据:', error.response.data);
      }
      throw error;
    }
  },
  
  // 获取领养申请详情
  getAdoptionDetails(applicationId) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    console.log(`获取领养申请详情: ID=${applicationId}`);

    return axios({
      url: `/adoption/details/${applicationId}`,
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      }
    });
  },
  
  // 更新领养申请状态
  updateAdoptionStatus(data) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    // 检查必要参数
    if (!data || !data.applicationId || !data.status) {
        return Promise.reject(new Error('缺少必要的参数: applicationId 或 status'));
    }

    // 构造查询参数
    const queryParams = new URLSearchParams({
        applicationId: data.applicationId,
        status: data.status
    });

    // 添加 notes 参数（如果存在）
    if (data.notes) {
        queryParams.append('notes', data.notes);
    }

    console.log(`更新领养申请状态，发送参数:`, data);

    return axios({
      url: `/adoption/update-status?${queryParams.toString()}`,
      method: 'put',
      headers: {
        // Content-Type 通常对于没有请求体的PUT请求不是必需的，但可以保留
        'Content-Type': 'application/json', 
        'token': token
      },
      // PUT 请求且参数在 URL 中，不需要请求体
      // data: data 
    });
  },
  // 添加救助记录
  addRescueRecord(data) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    return axios({
      url: '/rescueRecords',
      method: 'post',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      data
    });
  },

  // 上传救助记录信息
  uploadRescueRecord(data) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    const shelterId = localStorage.getItem('shelterId');
    if (!shelterId) {
      return Promise.reject(new Error('未找到救助站ID，请重新登录'));
    }

    // 构造请求体，确保包含 shelterId, animalId, details
    const requestData = {
      shelterId: parseInt(shelterId, 10),
      animalId: parseInt(data.animalId, 10),
      details: data.details
    };

    console.log('上传救助记录，发送数据:', requestData);

    return axios({
      url: '/rescue/uploadRescueRecord',
      method: 'post',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      data: requestData
    });
  },

  // 上传流浪动物照片
  uploadAnimalPhoto(animalId, file) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    if (!animalId || !file) {
      return Promise.reject(new Error('缺少 animalId 或文件'));
    }

    const formData = new FormData();
    formData.append('animalId', animalId);
    formData.append('animalPhoto', file, file.name);

    console.log(`上传动物照片: animalId=${animalId}, 文件名=${file.name}`);

    return axios({
      url: '/animals/uploadAnimalPhoto',
      method: 'post',
      headers: {
        'Content-Type': 'multipart/form-data',
        'token': token
      },
      data: formData
    });
  },

  // 2.1.3 搜索动物信息
  searchAnimals(params) {
    const token = localStorage.getItem('token');
    console.log('搜索动物使用的token:', token);
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    // 过滤掉值为空或 null 的参数
    const filteredParams = Object.entries(params).reduce((acc, [key, value]) => {
      if (value !== null && value !== '') {
        acc[key] = value;
      }
      return acc;
    }, {});
    
    console.log('发送搜索动物请求，参数:', filteredParams);
    
    return axios({
      url: '/animals/search',
      method: 'get',
      headers: {
        'Content-Type': 'application/json;charset=UTF-8',
        'token': token
      },
      params: filteredParams // 使用过滤后的参数
    });
  },

  // 2.1.4 根据动物ID查询某个流浪动物的信息
  getAnimalInfo(animalId) {
    const token = localStorage.getItem('token');
    console.log('查询单个动物信息使用的token:', token);
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    const id = parseInt(animalId, 10);
    if (isNaN(id)) {
        return Promise.reject(new Error('无效的动物ID'));
    }
    
    console.log('发送查询单个动物信息请求，animalId:', id);
    
    return axios({
      url: '/animals/getAnimalInfo',
      method: 'get',
      headers: {
        'Content-Type': 'application/json;charset=UTF-8',
        'token': token
      },
      params: { animalId: id }
    });
  },

  // 2.1.5 查询某救助站的所有流浪动物信息 (原 getAnimals 函数修改)
  getAnimalsInShelter(shelterId) { // 参数改为 shelterId
    const token = localStorage.getItem('token');
    console.log('获取救助站动物列表使用的token:', token);
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    const id = parseInt(shelterId, 10);
    if (isNaN(id)) {
        return Promise.reject(new Error('无效的救助站ID'));
    }
    
    console.log('发送获取救助站动物列表请求，shelterId:', id);
    
    return axios({
      url: '/animals/getAnimalsInShelter', // 修改 URL
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      params: {
        shelterId: id, // 使用参数 shelterId <-- 添加逗号
        // 重新添加分页参数，尝试获取更多记录
        page: 1, 
        limit: 1000 
      }
    });
  },

  // 函数：获取初始动物列表 (使用旧的 /animals/getAnimals 端点)
  fetchInitialAnimalList() {
    const token = localStorage.getItem('token');
    console.log('获取初始动物列表使用的token:', token);
    
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }
    
    // 注意：这个端点根据文档和之前的行为，似乎不需要 shelterId
    console.log('发送获取初始动物列表请求 (使用 /animals/getAnimals)');
    
    return axios({
      url: '/animals/getAnimals', // 使用旧的端点
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      },
      params: {
        page: 1, // 可以保留分页，或根据需要调整
        limit: 1000 // 设置较大的 limit 尝试获取所有
      }
    });
  },

  // 4.1.5 根据用户ID查询领养记录
  getAdoptionRecordsByUserId(userId) {
    const token = localStorage.getItem('token');
    if (!token) {
      return Promise.reject(new Error('未登录，请先登录'));
    }

    const id = parseInt(userId, 10);
    if (isNaN(id)) {
      return Promise.reject(new Error('无效的用户ID'));
    }

    console.log(`查询用户 ${id} 的领养记录`);

    return axios({
      url: `/adoption/user/${id}`,
      method: 'get',
      headers: {
        'Content-Type': 'application/json',
        'token': token
      }
    });
  },
};