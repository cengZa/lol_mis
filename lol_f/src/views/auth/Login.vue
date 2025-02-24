<script setup>
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { User, Lock } from '@element-plus/icons-vue'

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const router = useRouter()
const loading = ref(false)

const formRef = ref(null)
const authStore = useAuthStore()

const login = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (validationError) {
    ElMessage.error('请检查输入项是否填写正确')
    return
  }

  loading.value = true
  try {
    await authStore.login(form.value.username, form.value.password)
    ElMessage.success('登录成功')
    router.push('/')   // 登录后跳转到主布局 => '/'
  } catch (err) {
    console.warn(err)
    formRef.value?.$el.classList.add('shake-error')
    setTimeout(() => {
      formRef.value?.$el.classList.remove('shake-error')
    }, 600)
    form.value.password = ''
    const message = err.response?.data?.message || '登录失败，请检查网络连接'
    ElMessage.error({
      message,
      grouping: true,
      showClose: true
    })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <img src="@/assets/logo.svg" class="logo" alt="系统logo">
        <h2 class="title">欢迎登录</h2>
        <p class="subtitle">请输入您的账号密码开始使用</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="login" class="login-form">
        <!-- 账号输入 -->
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" autofocus :prefix-icon="User" size="large" />
        </el-form-item>

        <!-- 密码输入 -->
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock"
            size="large" />
        </el-form-item>

        <!-- 登录按钮 -->
        <el-button type="primary" :loading="loading" @click="login" class="login-btn" size="large">
          {{ loading ? '登录中...' : '立即登录' }}
        </el-button>

        <div class="login-links">
          <router-link to="/auth/register" class="link-item">
            还没有账号？立即注册
          </router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>


<style scoped>
/* 调试变量区 -- 通过修改这些变量快速调整界面 */
:root {
  /* 容器尺寸 */
  --login-card-width: 480px;  /* 主卡片宽度 */
  --login-card-padding: 2.5rem; /* 卡片内边距 */
  --input-height: 48px;      /* 输入框高度 */
  
  /* 颜色系统 */
  --primary-color: #409eff;  /* 主色调 */
  --error-color: #f56c6c;    /* 错误状态色 */
  --link-hover-color: #337ecc; /* 链接悬停色 */
  
  /* 动画参数 */
  --transition-speed: 0.3s;  /* 过渡动画速度 */
  --shake-animation: shake 0.6s ease-in-out; /* 错误抖动动画 */
}

.login-container {
  /* 保持原有样式 */
  min-height: 100vh;
  width: 75%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f4f5f7 0%, #a0c2ef 100%); /* 更柔和的渐变 */
}

.login-card {
  width: min(90%, var(--login-card-width)); /* 响应式宽度限制 */
  height: auto; /* 改为自动高度 */
  min-height: 500px; /* 最小高度保证视觉平衡 */
  padding: var(--login-card-padding);
  box-shadow: 
    0 12px 24px -6px rgba(0,0,0,0.1), /* 多层阴影增加层次感 */
    0 8px 16px -4px rgba(0,0,0,0.05);
}

.login-header {
  margin-bottom: 2.5rem; /* 使用rem单位保持比例 */
}

.logo {
  width: clamp(72px, 15vw, 96px); /* 响应式尺寸 */
  height: clamp(72px, 15vw, 96px);
  margin-bottom: 1.25rem;
}

.title {
  font-size: clamp(1.5rem, 4vw, 2rem); /* 响应式字体 */
  margin: 1rem 0 0.5rem;
}

.subtitle {
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 1.5rem; /* 增加表单项间距 */
    
    /* 错误状态 */
    &.is-error {
      .el-input__wrapper {
        box-shadow: 0 0 0 1px var(--error-color) !important;
        animation: var(--shake-animation);
      }
    }
  }

  :deep(.el-input__wrapper) {
    height: var(--input-height);
    transition: all var(--transition-speed);
    
    &:hover {
      box-shadow: 0 0 0 1px var(--primary-color);
    }
    
    &:focus-within {
      box-shadow: 0 0 0 2px var(--primary-color);
    }
  }
}

.login-btn {
  height: var(--input-height);
  font-size: 1rem;
  transition: 
    transform 0.2s ease,
    opacity 0.2s ease;
  
  &:active {
    transform: scale(0.98);
  }
  
  &[disabled] {
    opacity: 0.7;
  }
}

.login-links {
  margin-top: 2rem;
}

.link-item {
  color: var(--primary-color);
  
  &:hover {
    color: var(--link-hover-color);
    text-decoration: underline;
  }
}

/* 移动端优化 */
@media (max-width: 640px) {
  .login-card {
    --login-card-padding: 1.5rem;
    min-height: auto;
    padding-top: 3rem;
    padding-bottom: 3rem;
  }
  
  .login-form {
    :deep(.el-form-item) {
      margin-bottom: 1.25rem;
    }
  }
}
/* 错误状态动画 */
@keyframes shake {

  0%,
  100% {
    transform: translateX(0);
  }

  20% {
    transform: translateX(-10px);
  }

  40% {
    transform: translateX(10px);
  }

  60% {
    transform: translateX(-10px);
  }

  80% {
    transform: translateX(10px);
  }
}

.shake-error {
  animation: shake 0.6s ease-in-out;
}
</style>