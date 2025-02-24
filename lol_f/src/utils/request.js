import { useAuthStore } from "@/stores/auth";
import axios from "axios";

const service = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 3600
})

// 请求拦截器
service.interceptors.request.use(config =>{
    const token = localStorage.getItem('token')
    console.log('在请求拦截器中 得到token并尝试加入header {}',token);
    if(token){
        config.headers.Authorization = token
    }
    return config
})

// 响应拦截器
service.interceptors.response.use(
    response => {
        const res = response.data;
        if(res.code != 200){
            return Promise.reject(new Error(res.message || 'Error'));
        }
        return res;
    },
    error => {
        const auth = useAuthStore();
        if(error.response?.status === 401){
            console.warn('Token失效, 强制退出 401');
            auth.logout();
            router.push("/login");  // 跳转到登录页
        }
        return Promise.reject(error);
    }
)

export default service