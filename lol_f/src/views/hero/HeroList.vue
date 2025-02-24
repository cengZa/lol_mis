<script setup>
import service from '@/utils/request'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'


const heroes = ref([])

const pageNo = ref(1)
const pageSize = ref(8)
const total = ref(0)

const searchForm = reactive({
    heroName: '',
    heroTitle: '',
    heroType: '',
    battleRoute: ''
})


const totalPages = computed(() => {
    if (pageSize.value === 0) return 0
    return Math.ceil(total.value / pageSize.value)
})

const router = useRouter()
const loading = ref(false)

/**
 * 
 * @param {number} page - 指定跳转的页码
 */
async function fetchHeroes(page = 1) {
    try {
        loading.value = true
        pageNo.value = page

        // 如果没填任何搜索条件，就使用 /hero/list 接口；否则用 /hero/advancedSearch
        const useAdvancedSearch = (
            searchForm.heroName ||
            searchForm.heroTitle ||
            searchForm.heroType ||
            searchForm.battleRoute
        )

        const url = useAdvancedSearch ? '/hero/advancedSearch' : '/hero/list'
        // 构造请求参数
        const params = {
            pageNo: pageNo.value,
            pageSize: pageSize.value
        }
        if (useAdvancedSearch) {
            // 高级搜索的额外参数
            if (searchForm.heroName) params.heroName = searchForm.heroName
            if (searchForm.heroTitle) params.heroTitle = searchForm.heroTitle
            if (searchForm.heroType) params.heroType = searchForm.heroType
            if (searchForm.battleRoute) params.battleRoute = searchForm.battleRoute
        }

        const res = await service.get(url, { params })
        // 后端返回结构: { code, message, data }, data即Page<Hero>
        console.log('获取的信息为: ', res)

        if (res.code === 200) {
            heroes.value = res.data.records
            console.log('heroes: ', heroes)
            total.value = res.data.total
        } else {
            ElMessage.error(res.message || '查询失败')
        }
    } catch (err) {
        console.error('获取英雄列表失败: ', err)
        ElMessage.error('获取英雄列表失败')
    } finally {
        loading.value = false
    }
}

function goDetail(heroId) {
    // console.log('点击英雄卡片, heroId:', heroId)
    // router.push({ path: `/heroDetail/${heroId}` })
    router.push({ name: 'HeroDetail', params: { heroId } })
}

function doSearch() {
    fetchHeroes(1)
}

function resetSearch() {
    searchForm.heroName = ''
    searchForm.heroTitle = ''
    searchForm.heroType = ''
    searchForm.battleRoute = ''
    fetchHeroes(1) // 重置后重新加载
}

onMounted(() => {
    fetchHeroes(pageNo.value)
})

</script>


<template>
    <div class="hero-list-container" v-loading="loading" element-loading-text="正在加载...">
        <!-- 页面头部，与首页作区分 -->
        <el-page-header content="英雄列表" @back="$router.push('/')" class="list-page-header" />

        <!-- 搜索区域 -->
        <el-form :inline="true" :model="searchForm" label-width="80px" class="search-form">
            <el-form-item label="英雄名称">
                <el-input v-model="searchForm.heroName" placeholder="英雄名称" clearable />
            </el-form-item>

            <el-form-item label="英雄称号">
                <el-input v-model="searchForm.heroTitle" placeholder="英雄称号" clearable />
            </el-form-item>

            <el-form-item label="英雄类型">
                <el-select v-model="searchForm.heroType" placeholder="类型" clearable style="width: 120px">
                    <el-option label="控制者" value="CONTROLLER" />
                    <el-option label="战士" value="FIGHTER" />
                    <el-option label="法师" value="MAGE" />
                    <el-option label="射手" value="MARKSMAN" />
                    <el-option label="刺客" value="SLAYER" />
                    <el-option label="坦克" value="TANK" />
                </el-select>
            </el-form-item>

            <el-form-item label="战斗分路">
                <el-select v-model="searchForm.battleRoute" placeholder="分路" clearable style="width: 120px">
                    <el-option label="TOP" value="TOP" />
                    <el-option label="JUG" value="JUG" />
                    <el-option label="MID" value="MID" />
                    <el-option label="ADC" value="ADC" />
                    <el-option label="SUP" value="SUP" />
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="doSearch">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 英雄卡片列表 -->
        <el-row :gutter="20" class="card-list-row">
            <el-col :span="6" v-for="hero in heroes" :key="hero.id" class="card-col">
                <el-card class="hero-card" shadow="hover" @click="goDetail(hero.id)">
                    <!-- 图片自适应父容器 -->
                    <div class="hero-image-wrapper">
                        <img :src="hero.avatar" alt="hero-avatar" class="hero-image" />
                    </div>
                    <div class="hero-title">
                        <strong>{{ hero.heroTitle }}</strong>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 分页控件 -->
        <div v-if="total > 0" class="pagination-wrapper">
            <el-pagination background layout="prev, pager, next, ->, total" :total="total" :page-size="pageSize"
                :current-page="pageNo" @current-change="fetchHeroes" />
        </div>
    </div>
</template>

<style scoped>
.hero-list-container {
    padding: 20px;
    background-color: #e7e4e2;
}

.list-page-header {
    margin-bottom: 20px;
}

.search-form {
    margin-bottom: 20px;
}

.card-list-row {
    margin-bottom: 20px;
}

.hero-card {
    cursor: pointer;
    transition: all 0.3s ease;
}

.hero-card:hover {
    transform: translateY(-3px);
}

.hero-image-wrapper {
    width: 100%;
    position: relative;
    padding-top: 56.25%;
    overflow: hidden;
    border-radius: 4px;
    background-color: #f5f7fa;
}

.hero-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.hero-title {
    margin-top: 8px;
    text-align: center;
    font-size: 14px;
    color: #333;
}

.pagination-wrapper {
    text-align: center;
    margin-top: 20px;
}
</style>