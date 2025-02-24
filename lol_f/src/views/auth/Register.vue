<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import service from '@/utils/request'

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: ''
})

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('密码不能为空'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const submit = async () => {
  formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请检查表单填写是否正确')
      return
    }
    loading.value = true
    try {
      await service.post('/user/register', form)
      ElMessage.success('注册成功')
      // 注册完成后，自动跳转到登录页
      router.push('/auth/login')
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '注册失败，请重试')
    } finally {
      loading.value = false
    }
  })
}
</script>


<template>
  <div class="register-wrapper">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="register-form" @keyup.enter="submit">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">
          注册
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
/* 父容器，居中对齐 */
.register-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

/* 表单样式 */
.register-form {
  width: 400px;
  padding: 20px;
  border: 1px solid #dcdcdc;
  border-radius: 6px;
  background-color: #fff;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

/* 按钮样式，示例可根据需要自定义 */
.register-form .el-button {
  width: 100%;
}
</style>