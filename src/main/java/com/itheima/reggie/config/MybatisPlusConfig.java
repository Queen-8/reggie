package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 */
@Configuration
public class MybatisPlusConfig {
//    MybatisPlusInterceptor 拦截器
    @Bean // Spring 管理
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
//        通过 add 加入一个拦截器 PaginationInnerInterceptor,拦截器是MybatisPlus提供
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
//        return null;
        return mybatisPlusInterceptor;
    }
}