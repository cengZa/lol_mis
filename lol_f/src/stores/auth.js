import router from "@/router";
import service from "@/utils/request";
import { defineStore } from "pinia";
import { ref } from 'vue'

//? 既然token在localStorage了 为什么还要在useAuthStore里存着？
//* localStorage 持久化存储，保证页面刷新后保留数据，阻塞式同步读取
//* Pinia 应用运行时状态管理，响应式访问，页面会话期间有效, 所有组件共享同一份认证状态
//? pinia有什么用
export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || '');
    const userInfo = ref(null);

    // 登录
    const login = async (username, password) => {
        try{
            const res = await service.post('/user/login', null, {
                params: {username, rawPassword: password}
            })

            token.value = res.data;
            localStorage.setItem('token', res.data);
    
            console.warn('Get user token by auth.js', token);
    
            await fetchUserInfo();  // 登录成功后获取用户信息
        }catch (err) {
            let message = '登录失败 默认返回'
            if (err.response) {
              switch (err.response.status) {
                case 401:
                  message = '账号或密码错误'
                  break
                case 403:
                  message = '账号已被禁用'
                  break
                case 429:
                  message = '尝试次数过多，请稍后再试'
                  break
              }
            }
            throw new Error(message)
          }
    }

    // 获取用户信息
    const fetchUserInfo = async () => {
        userInfo.value = (await service.get('/user/userInfo')).data;
        console.warn('Get userInfo in auth.js', userInfo.value);
    }

    // 更新用户信息
    const updateUserInfo = (newInfo) => {
      userInfo.value = {...userInfo.value,...newInfo};
    }

    // 退出登录
    const logout = () => {
        token.value = '';
        userInfo.value = null;
        localStorage.removeItem('token');
        console.warn('用户登出，清除 Token');
        router.push('/auth/login');  // ? 还是这个问题 路由守卫为什么没办法自动跳转到登录页而是需要我手动显式跳转
    }

    return { token, userInfo, login, logout, fetchUserInfo, updateUserInfo}
})