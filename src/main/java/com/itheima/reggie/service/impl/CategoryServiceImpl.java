package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{


//        @Autowired    //注入
        private DishService dishService;
//        @Autowired
        private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断，当前分类是否关联了菜品或者套餐
     * @param id
     */
    @Override
    public void remove(Long id) { //这个接口下的实现方法

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加查询条件,根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
//        查询当前分类是否关联菜品，如果已经关联，则直接抛出业务异常
        if (count1 > 0) {
//            已经关联菜品，需要抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        传入的id和CategoryId是否匹配
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //查询当前分类是否关联套餐，如果已经关联，则直接抛出业务异常
        if(count2 > 0) {
            //已经关联套餐，需要抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
