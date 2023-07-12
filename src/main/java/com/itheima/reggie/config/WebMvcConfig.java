package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


@Slf4j  //加日志方便调试
@Configuration  //说明这个类是一个配置类
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /*
    * 设置静态资源映射【浏览器界面发出的url请求 --> 映射到 backend目录下的页面】访问成功
    * */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        /**
         * 设置映射文件和访问路径
         * 只要前端请求url中有 /backend/**
         * 就会被映射到 classpath:/backend
         */
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展 mvc 框架的消息转换器
     * spring mvc 的一个组件
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("消息转换器");
//        创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        设置对象转换器，底层使用 Jackson 将 java 对象转为 json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        将上面的消息转换器对象追加到 mvc 框架的转化器容器
        converters.add(0, messageConverter); //转换器有顺序,0表示优先使用该转换器
    }
}

