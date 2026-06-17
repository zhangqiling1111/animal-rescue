<template>    
  <list></list>
</template>
  
<script setup lang="ts">
  import { ref, reactive, toRefs, onMounted} from 'vue'
  import list from '../../components/admin/admininfo.vue'
</script>
<style scoped lang="scss">
  :deep(.el-dialog) {
    background: linear-gradient(180deg, #e5f3fe, #ffffff);
    border-radius: 12px;
  }
</style>