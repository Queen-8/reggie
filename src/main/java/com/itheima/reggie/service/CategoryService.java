package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id); //这个是接口,定义一个实现方法,在对应实现类里面使用
}
