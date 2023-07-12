package com.itheima.reggie.common;

/**
 * 自定义异常
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) { //message 是传入的提示信息
        super(message); //super 表示父类。super(message)调用了RuntimeException类的构造方法，并将参数message传递给父类构造方法
    }
}
