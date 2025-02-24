package com.genius.lol.util;

import cn.hutool.core.convert.Convert;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtl {

    // 存取Map， 在该线程保存多字段信息
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    public static Map<String, Object> getMap() {
        return THREAD_LOCAL.get();
    }

    public static void put(String key, Object value){
        THREAD_LOCAL.get().put(key, value);
    }

    public static void put(Map<String, Object> map){
        THREAD_LOCAL.get().putAll(map);
    }

    public static void clear(){
        THREAD_LOCAL.remove();
    }

    public static <T> T get(String key, Class<T> type){
        Object val = THREAD_LOCAL.get().get(key);
        return val == null ? null : type.cast(val);
    }

    public static Long getUid(){
        return Convert.toLong(THREAD_LOCAL.get().get("uid"));
    }

    public static void removeKey(String key) {
        THREAD_LOCAL.get().remove(key);
    }

}
