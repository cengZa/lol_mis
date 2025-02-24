<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const props = defineProps({
    dialogVisible: {
        type: Boolean,
        default: false
    }
})

const emits = defineEmits(['update:dialogVisible', 'refreshUserInfo'])

const dialogVisible = ref(props.dialogVisible)
const selectedFile = ref(null)
const previewUrl = ref('')
const loading = ref(false)

// 监听父组件传进来的 dialogVisible
watch(
    () => props.dialogVisible,
    newVal => {
        dialogVisible.value = newVal
    }
)

// 选择文件
function handleFileChange(file) {
    selectedFile.value = file.raw
    previewUrl.value = URL.createObjectURL(file.raw)
}

// 提交上传
async function submitUpload() {
    if (!selectedFile.value) {
        ElMessage.warning('请先选择文件')
        return
    }
    loading.value = true
    try {
        // 1. 上传到服务器或OSS
        const formData = new FormData()
        formData.append('avatar', selectedFile.value)
        let { data: uploadedUrl } = await service.post('/user/uploadAvatar', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })

        // 2. 更新数据库中头像链接
        await service.patch('/user/updateAvatar', null, {
            params: { avatarUrl: uploadedUrl }
        })

        ElMessage.success('头像上传更新成功')
        emits('refreshUserInfo')     // 告知父组件刷新用户信息
        closeDialog()
    } catch (error) {
        ElMessage.error('上传失败: ' + error.message)
    } finally {
        loading.value = false
    }
}

// 关闭对话框
function closeDialog() {
    dialogVisible.value = false
    emits('update:dialogVisible', false) // 通知父组件更新
    selectedFile.value = null
    previewUrl.value = ''
}
</script>

<template>
    <el-dialog v-model="dialogVisible" title="更换头像" width="450px">
        <el-upload class="avatar-uploader" drag action="" :auto-upload="false" :show-file-list="false"
            :on-change="handleFileChange">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
            </div>
        </el-upload>

        <!-- 预览 -->
        <div v-if="previewUrl" class="preview-container">
            <el-image :src="previewUrl" alt="avatar preview" style="max-width: 200px" />
        </div>

        <span slot="footer" class="dialog-footer">
            <el-button @click="closeDialog">取消</el-button>
            <el-button type="primary" :loading="loading" @click="submitUpload">
                确认上传
            </el-button>
        </span>
    </el-dialog>
</template>

<style scoped>
.avatar-uploader {
    margin-top: 20px;
}

.preview-container {
    margin-top: 16px;
    text-align: center;
}
</style>
