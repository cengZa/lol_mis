<script setup>

import { ref } from 'vue'
import { ElForm, ElMessage } from 'element-plus'
import service from "@/utils/request";

const form = ref({
    oldPwd: '',
    newPwd: '',
    confirmPwd: ''
})

// 自定义校验
const validateConfirm = (rule, value, callback) => {
    if (value !== form.value.newPwd) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 1, message: '至少1位', trigger: 'blur' }
  ],
  confirmPwd: [{ required: true, validator: validateConfirm, trigger: 'blur' }]
}

const loading = ref(false)
const submit = async () => {
  loading.value = true
  try {
    await service.patch('/user/changePassword', form.value)
    ElMessage.success('密码修改成功')
    // 清空表单
    form.value = { oldPwd: '', newPwd: '', confirmPwd: '' }
  } catch(error){
    ElMessage.error('密码修改失败: ' + error.message);
  }
  finally {
    loading.value = false
  }
}

</script>

<template>
  <div class="change-password">
    <el-form :model="form" :rules="rules" ref="formRef">
      <el-form-item label="原密码" prop="oldPwd">
        <el-input v-model="form.oldPwd" type="password" placeholder="请输入原密码" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPwd">
        <el-input v-model="form.newPwd" type="password" placeholder="请输入新密码" />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPwd">
        <el-input v-model="form.confirmPwd" type="password" placeholder="请确认新密码" />
      </el-form-item>
      <el-button type="primary" :loading="loading" @click="submit">提交</el-button>
    </el-form>
  </div>
</template>