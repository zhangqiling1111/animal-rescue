<template>
  <div>
    <h2>修改动物信息</h2>
    <el-form :model="form" label-width="120px">
      <el-form-item label="品种">
        <el-input v-model="form.breed" />
      </el-form-item>
      <el-form-item label="年龄（月）">
        <el-input v-model="form.age" type="number" />
      </el-form-item>
      <el-form-item label="健康状况">
        <el-input v-model="form.health_status" />
      </el-form-item>
      <el-form-item label="是否可领养">
        <el-switch v-model="form.is_adoptable" />
      </el-form-item>
      <el-button type="primary" @click="submitForm">提交</el-button>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import shelterService from '@/service/shelter.js';

const form = ref({
  breed: '',
  age: '',
  health_status: '',
  is_adoptable: false,
});

const animalId = 1; // 假设动物 ID 为 1

onMounted(async () => {
  const response = await shelterService.getAnimals(animalId);
  Object.assign(form.value, response.data);
});

const submitForm = async () => {
  try {
    await shelterService.updateAnimal(animalId, form.value);
    alert('动物信息修改成功');
  } catch (error) {
    console.error('修改失败', error);
  }
};
</script>