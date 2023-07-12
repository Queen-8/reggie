package com.itheima.reggie.common;

/**
 * 基于 ThreadLocal 封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {

    //指定一个Long的泛型,因为用户的id是Long
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    /**
     * 设置值
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获得值 -- 作用范围都是在某个线程之内，多次请求也不会混淆
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

}
