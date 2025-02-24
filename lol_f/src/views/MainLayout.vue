<script setup>
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import UploadAvatarDialog from '@/components/avatar/UploadAvatarDialog.vue'


const authStore = useAuthStore()
const router = useRouter()

const dialogVisible = ref(false)
const avatarTooltipText = '更换头像'

const defaultAvatarUrl = 'https://lol-bucket.oss-cn-beijing.aliyuncs.com/9e77c3fa-b5a7-4960-896d-63ce51f46a11.jpg'


// 点击退出
const logout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  // 如果需要跳转回登录:
  router.push('/auth/login')
}

// 刷新用户信息
async function refreshUserInfoMethod() {
  try {
    await authStore.fetchUserInfo()
    ElMessage.success('头像更新成功')
  } catch (error) {
    ElMessage.error('刷新信息失败: ' + error.message)
  }
}

</script>

<template>
  <div class="main-layout">
    <!-- 左侧导航 / 用户管理栏 -->
    <aside class="side-menu">
      <div class="side-menu-header">User Center</div>

      <!-- 顶部用户信息区 -->
      <header class="header-bar">
        <!-- 使用 el-tooltip 包裹 avatar，鼠标悬停时显示提示 -->
        <el-tooltip :content="avatarTooltipText" effect="dark" placement="right">
          <el-avatar :src="authStore.userInfo?.avatar || defaultAvatarUrl" class="user-avatar"
            @click="dialogVisible = true">
            <!-- 如果avatar为空，则显示默认用户图标 -->
            <template #icon>
              <i class="el-icon-user"></i>
            </template>
          </el-avatar>
        </el-tooltip>
        <span class="nickname">
          {{ authStore.userInfo?.nickname || '游客' }}
        </span>
      </header>

      <!-- 点击后，弹出上传头像对话框 -->
      <UploadAvatarDialog :dialogVisible="dialogVisible" @update:dialogVisible="dialogVisible = $event"
        @refreshUserInfo="refreshUserInfoMethod" />

      <!-- 若未登录则显示 "登录/注册" -->
      <router-link v-if="!authStore.token" to="/auth/login" class="menu-item">
        登录
      </router-link>
      <router-link v-if="!authStore.token" to="/auth/register" class="menu-item">
        注册
      </router-link>

      <!-- 若已登录则显示其他管理项 -->
      <router-link v-if="authStore.token" to="/profile" class="menu-item">
        个人信息
      </router-link>
      <router-link v-if="authStore.token" to="/change-password" class="menu-item">
        修改密码
      </router-link>
      <router-link v-if="authStore.token" to="/heroes" class="menu-item">
        英雄列表
      </router-link>

      <el-button v-if="authStore.token" link class="menu-item logout-btn" @click="logout">
        退出登录
      </el-button>
    </aside>

    <!-- 右侧主要内容区 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.main-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

/* 左侧区域 */
.side-menu {
  width: 220px;
  background-color: #2f3c48;
  color: #fff;
  display: flex;
  flex-direction: column;
  padding: 1rem;
}

.side-menu-header {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 1rem;
}

/* 顶部用户信息容器 */
.header-bar {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

/* 头像 */
.user-avatar {
  cursor: pointer;
  margin-right: 0.75rem;
  transition: transform 0.3s;
}

.user-avatar:hover {
  transform: scale(1.05);
}

/* 昵称 */
.nickname {
  color: #fff;
  font-weight: 500;
}

/* 左侧菜单项 */
.menu-item {
  display: block;
  padding: 0.5rem 0.75rem;
  color: #fff;
  text-decoration: none;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  transition: background-color 0.3s;
}

.menu-item:hover {
  background-color: #414f5a;
}

.menu-item.router-link-active {
  background-color: #1e90ff;
}

/* 退出按钮 */
.logout-btn {
  color: #fff;
  text-align: left;
  width: 100%;
  background: none;
}

/* 右侧内容 */
.main-content {
  flex: 1;
  overflow-y: auto;
  background-color: #fafafa;
  padding: 1.5rem;
}
</style>