package com.avalon.packer.context;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 存放当前上下文对象 线程副本
 *
 * @author wangxb
 * @date 2021/03/24 02:51
 **/

@Slf4j
public abstract class ZzContextHandler {

    public static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            threadLocal.set(map);
        }
        return map.get(key);
    }

    public static void remove() {
        threadLocal.remove();
    }

    protected static String returnObjectValue(Object value) {
        return value == null ? null : value.toString();
    }

}
