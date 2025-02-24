<script setup>
import { ref, onMounted, toRaw } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const authStore = useAuthStore();
const userLoaded = ref(false);
const form = ref({
  username: '',
  nickname: '',
  email: ''
})


onMounted(() => {
  console.log(' 在挂载Profile.vue时访问authStore.userInfo: ',authStore.userInfo);
  if (authStore.userInfo) {
    form.value.username = authStore.userInfo.username || ''
    form.value.nickname = authStore.userInfo.nickname || ''
    form.value.email = authStore.userInfo.email || ''
    console.log('在挂载Profile.vue时，表单访问userInfo, form.value: ', form.value);
  }
  userLoaded.value = true
})


const handleUpdate = async () => {
  try {
    /** 
     * *直接向后端发送 Vue 响应式对象会导致 JSON.stringify 出现死循环错误。
     */
    const payload = toRaw(form.value)
    const res = await service.patch('/user/update', payload)
    
    authStore.updateUserInfo(res.data)

    ElMessage.success('信息更新成功');
  } catch (err) {
    // 如果后端返回 msg，就提示具体信息
    // 或者使用更详细的方式 err.response?.data?.message
    ElMessage.error(`更新失败: ${err}`)
  }
}
</script>

<template>
  <div class="profile">
    <h3>个人信息管理</h3>
    <el-form
      ref="formRef"
      :model="form"
      label-width="100px"
      v-if="userLoaded"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-button
        type="primary"
        @click="handleUpdate"
      >
        保存修改
      </el-button>
    </el-form>
  </div>
</template>

<style scoped>
.profile {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
}
</style>