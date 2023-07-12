package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.reggie.common.BaseContext;
//import jdk.vm.ci.meta.Local;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


//import com.itheima.reggie.filter;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j //记录日志
@Component  //提供依赖注入和AOP,让Spring框架来管理

public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * 自定义元数据对象处理器
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        /**
         * 插入操作自动填充
         */
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        /**
         * 获取当前登录用户的 id【id是Long型】,id 要从 Session 里获取, Session要获取的前提是能获取 request/Session对象
         */
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

        /**
     * 更新修改操作自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();
        log.info("update 线程id为：{} ",id);

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
