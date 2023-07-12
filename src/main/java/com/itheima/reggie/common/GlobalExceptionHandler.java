package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @return
     */
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
//        log.error(ex.getMessage());
//
//        if ((ex.getMessage().contains(("Duplicate entry")){
//            String[] split = ex.getMessage().split(" ");
//            String msg = split[2] + "已存在";
//            return R.error(msg);
//        }
//        return R.error("失败");
//    }
    /**
     * 异常处理方法 --- 只要标注有 RestController.class 和 Controller.class 下的类有异常都会被处理
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){ // 表示处理sql的这个异常，输入重复账号就会用这个异常检测到
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){ //如果异常信息中包含 Duplicate entry
            /**
             * 把输入的重复 username 截出：
             * 1.首先将整个异常句子分割成数组,username 是属于数组的第三个
             * 2.拼接显示 username 已存在,并返回 error
             */
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在"; //拼接字符串
            return R.error(msg);
        }
        return R.error("未知错误"); //说明不是上面的重复异常,则当前错误无法定位,返回：未知错误
    }



    /**
     * 异常处理方法--自定义异常
     * @return
     */
    @ExceptionHandler(CustomException.class)//() 标注处理哪种异常,此处是一个自定义异常
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        //获取 CategoryServiceImpl 中 抛出的异常 throw new CustomException("当前分类下关联了菜品,不能删除");
        return R.error(ex.getMessage());
    }
}
