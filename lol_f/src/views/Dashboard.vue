<script setup>
import { onMounted, computed, ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import UploadAvatarDialog from '@/components/avatar/UploadAvatarDialog.vue'

// Pinia Store
const authStore = useAuthStore()

// 包装 userInfo，避免空指针
const computedUserInfo = computed(() => authStore.userInfo)

// 控制上传头像弹窗可见性
const dialogVisible = ref(false)

// 默认头像
const defaultAvatarUrl = 'https://lol-bucket.oss-cn-beijing.aliyuncs.com/9e77c3fa-b5a7-4960-896d-63ce51f46a11.jpg'

// 刷新用户信息
async function refreshUserInfo() {
  try {
    console.log('Refresh user info triggered.')
    await authStore.fetchUserInfo()
  } catch (error) {
    ElMessage.error('刷新用户信息失败: ' + error.message)
  }
}

// 组件挂载时尝试获取用户信息
onMounted(async () => {
  console.log('挂载Dashboard，检查 userInfo:', computedUserInfo.value)
  try {
    if (!computedUserInfo.value) {
      console.warn("userInfo为空, 尝试fetch后 ->", computedUserInfo.value)
      await authStore.fetchUserInfo()
    }
  } catch (error) {
    ElMessage.error('用户信息加载失败，请稍后重试。')
  }
})

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      authStore.logout()
      router.push('/login')
      ElMessage.success('退出登录成功')
    })
    .catch(() => {
      // 用户取消
      console.log('用户取消了退出操作')
    })
}
</script>

<template>
  <div class="dashboard">
    <!-- header区域 -->
    <header class="dashboard-header">
      <div class="user-info">
        <!-- 用户头像 -->
        <el-avatar :src="authStore.userInfo?.avatar || defaultAvatarUrl" class="avatar" fit="cover"
          @click="dialogVisible = true">
          <template #icon>
            <User />
          </template>
        </el-avatar>
        <!-- 修改头像弹框 -->
        <UploadAvatarDialog v-model:dialogVisible="dialogVisible" @refreshUserInfo="refreshUserInfo" />

        <span class="nickname">
          {{ computedUserInfo.value?.nickname || '游客' }}
        </span>
      </div>

      <el-button link class="logout-btn" @click="handleLogout">退出登录</el-button>
    </header>

    <!-- 导航菜单 -->
    <nav class="dashboard-nav">
      <router-link to="/profile" class="nav-item">个人信息</router-link>
      <router-link to="/change-password" class="nav-item">修改密码</router-link>
      <router-link to="/heroes" class="nav-item">英雄列表</router-link>
    </nav>

    <!-- 主体内容区：自适应占满剩余区域 -->
    <main class="dashboard-content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
/* 1. 整体外层容器自适应，撑满屏幕高度 */
.dashboard {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  /* 覆盖大部分浏览器可视区域 */
  background-color: #f5f7fa;
  /* 给父容器一个柔和的底色，提升页面整体感 */
}

/* 2. 头部区域布局：横向排列 + 垂直居中 */
.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1.5rem;
  /* 增加内边距，显得更大气 */
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

/* 用户信息区域：头像 + 昵称 */
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  /* 鼠标移上去提示可点击 */
}

/* 头像外观 */
.avatar {
  margin-right: 0.75rem;
  /* 适当加大间距 */
  transition: transform 0.3s;
}

.avatar:hover {
  transform: scale(1.05);
  /* 悬停时微放大，提示可点击 */
}

/* 昵称文字 */
.nickname {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  /* 姓名过长时保持在一行 */
}

/* 退出按钮 */
.logout-btn {
  color: #909399;
  font-weight: 500;
  transition: color 0.3s;
}

.logout-btn:hover {
  color: #c20e0e;
  /* 提示性更强的红色 */
}

/* 3. 导航区域 */
.dashboard-nav {
  display: flex;
  align-items: center;
  padding: 0.5rem 1.5rem;
  /* 与头部保持一致的边距 */
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  gap: 1rem;
  /* 直接使用 gap 来控制子元素间距 */
}

/* 导航链接外观 */
.nav-item {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  position: relative;
}

.nav-item.router-link-active {
  color: #409eff;
  /* 激活状态突出 */
}

/* 鼠标悬停效果 */
.nav-item:hover {
  color: #666;
}

/* 4. 主体内容区：flex:1 占满剩余空间，加滚动 */
.dashboard-content {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
  background-color: #f5f7fa;
  /* 适当留白，让内容呼吸 */
}

</style>
