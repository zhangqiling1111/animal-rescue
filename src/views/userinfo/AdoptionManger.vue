<template>
  <h2 class="page-title">领养申请记录</h2>
  <list></list>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted} from 'vue'
import list from '../../components/user/AdoptionRecrd.vue'
</script>
<style scoped lang="scss">
.page-title {
  margin-left: 30px;
  color: #007bff; // 标题颜色为红色
  margin-bottom: 10px;
}
:deep(.el-dialog) {
  background: linear-gradient(180deg, #e5f3fe, #ffffff);
    border-radius: 12px;
  }
</style>