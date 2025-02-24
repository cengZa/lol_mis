<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

// 1. hero从空对象改为 ref(null) 或 ref({})
//   - 如果后端没有查到英雄，hero可能是null或{}。
//   - 这里就让它默认为null，方便判断是否有数据。
const hero = ref(null)
const loading = ref(false)

const route = useRoute()
const router = useRouter()

/**
 * 获取英雄详情
 * @param {Number|String} heroId
 */
async function fetchHeroDetail(heroId) {
  loading.value = true
  try {
    const res = await service.get(`/hero/detail/${heroId}`)
    if (res.code === 200) {
      hero.value = res.data
      console.log('获取英雄详情信息: ', hero.value)
    } else {
      ElMessage.error(res.message || '获取英雄详情失败(默认)')
      // 给用户提示后跳转回列表
      router.push('/heroes')
    }
  } catch (error) {
    console.error('获取英雄详情失败: ', error)
    ElMessage.error('获取英雄详情失败')
    router.push('/heroes')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const heroId = route.params.heroId
  if (!heroId) {
    console.warn('无效英雄ID')
    // 如果路由参数无效，直接回列表
    router.push('/heroes')
  } else {
    fetchHeroDetail(heroId)
  }
})
</script>

<template>
  <!-- 外层容器：渐变背景、统一留白，让整体更美观 -->
  <div class="hero-detail-container" v-loading="loading" element-loading-text="疯狂加载中...">
    
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/heroes' }">英雄列表</el-breadcrumb-item>
      <!-- hero可能是null，所以用可选链 -->
      <el-breadcrumb-item>{{ hero?.heroName || '详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 主体内容区域 -->
    <div class="detail-main">
      <!-- 如果hero有数据(非null)，显示详情卡片，否则给提示 -->
      <template v-if="hero">
        <el-card class="hero-card" shadow="hover">
          <div class="hero-content">
            <!-- 图片部分: 用ElementPlus的el-image可支持预览 -->
            <el-image
              v-if="hero.avatar"
              :src="hero.avatar"
              fit="cover"
              class="hero-avatar"
              :preview-src-list="[hero.avatar]"
            >
              <!-- fallback：如果图片加载失败可显示一个占位 -->
              <template #error>
                <div class="image-error">No Image</div>
              </template>
            </el-image>

            <div class="info-area">
              <!-- 英雄名 + 称号：加一点酷炫文字 -->
              <h1 class="hero-name neon-text">
                {{ hero.heroName }}
                <small class="subtitle">- {{ hero.heroTitle }}</small>
              </h1>

              <p class="hero-meta">
                <span>类型：<strong>{{ hero.heroType || '未知' }}</strong></span>
                <span>分路：<strong>{{ hero.battleRoute || '未知' }}</strong></span>
              </p>

              <p class="hero-story">
                {{ hero.backgroundStory || '这位英雄暂时没有背景故事...' }}
              </p>

              <!-- 给用户一个返回按钮，方便操作 -->
              <el-button type="primary" icon="el-icon-arrow-left" @click="$router.push('/heroes')">
                返回列表
              </el-button>
            </div>
          </div>
        </el-card>
      </template>

      <template v-else>
        <!-- 英雄不存在时显示 -->
        <div class="no-hero">
          <p class="fancy-text">英雄不存在或已被删除!</p>
          <el-button type="warning" icon="el-icon-arrow-left" @click="$router.push('/heroes')">
            返回列表
          </el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
/* 
  整体容器:
  1. 设置一个渐变背景
  2. 让内容居中、适度留白
*/
.hero-detail-container {
  min-height: 100vh;
  padding: 30px;
  /* 渐变背景，可自行修改颜色 */
  background: linear-gradient(135deg, #f3f1f1, #d1cfcf, #eee);
}

/* 面包屑与顶部布局 */
.breadcrumb {
  margin-bottom: 20px;
  font-size: 14px;
}

/* 卡片主体 */
.detail-main {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

/* hero-card外观 */
.hero-card {
  max-width: 900px;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  padding: 20px;
  background-color: #fff;
  animation: fadeIn 0.5s ease forwards;
}

/* 内容区flex布局: 左侧图, 右侧信息 */
.hero-content {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.hero-avatar {
  width: 300px;
  height: 300px;
  border-radius: 8px;
  object-fit: cover;
  /* 给图片一个轻微阴影 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

/* 右侧信息区域 */
.info-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  /* 让文字更优雅 */
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

/* 英雄名 + 称号: 做一点“酷炫”文字效果 */
.hero-name {
  margin: 0;
  font-size: 26px;
  font-weight: bold;
  color: #333;
  line-height: 1.2;
  position: relative;
}

/* 副标题(英雄Title) */
.subtitle {
  font-size: 14px;
  margin-left: 8px;
  font-weight: normal;
  color: #888;
}

/* 可加个“霓虹”效果 */
.neon-text {
  color: #ff0;
  text-shadow:
    0 0 2px #ff0,
    0 0 10px #ff0,
    0 0 20px #f0f,
    0 0 30px #f0f,
    0 0 40px #f0f,
    0 0 50px #f0f,
    0 0 60px #f0f;
  animation: neonGlow 2s infinite alternate;
}

/* 霓虹文字动画 */
@keyframes neonGlow {
  0% {
    text-shadow:
      0 0 2px #ff0,
      0 0 10px #ff0,
      0 0 20px #f0f,
      0 0 30px #f0f,
      0 0 40px #0ff,
      0 0 50px #0ff,
      0 0 60px #0ff;
  }
  100% {
    text-shadow:
      0 0 2px #ff0,
      0 0 10px #ff0,
      0 0 20px #ff0,
      0 0 30px #ff0,
      0 0 40px #ff0,
      0 0 50px #ff0,
      0 0 60px #ff0;
  }
}

/* 额外动画 */
@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateY(10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 类型&分路等信息 */
.hero-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #555;
}

/* 背景故事的段落 */
.hero-story {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin: 6px 0 0;
  white-space: pre-wrap;
}

/* 当没有hero时的提示 */
.no-hero {
  text-align: center;
  margin: 50px auto;
}

/* 给那行提示文字做个小动画或渐变 */
.fancy-text {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 20px;
  animation: flicker 2s infinite alternate;
}

@keyframes flicker {
  0% {
    opacity: 0.6;
  }
  100% {
    opacity: 1;
  }
}

/* 如果图片加载失败时显示 */
.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  background: #eee;
}
</style>
