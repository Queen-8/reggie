package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {//提交的dishDto是 json数据，所以要加注解@RequestBody
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页  根据id 查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        /**添加一个过滤条件：当这个name != null 时，进行模糊查询，
         * Dish::getName：这是一个方法引用，表示获取Dish对象的名称属性。
         * name：这是一个变量或值，表示要进行模糊查询的目标字符串。
         */
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件 -- 根据更新时间降序排
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");//records是拷贝时被忽略拷贝的属性，因为records是一个集合，另外拷贝

        List<Dish> records = pageInfo.getRecords();//拿出records的集合对象

        List<DishDto>  list = records.stream().map((item) -> { //分页页面不只展示dish,还要展示菜品分类名称
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();

            //根据id查分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName(); //查询分类名称
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * @PathVariable 将 URL 中的路径变量绑定到方法的参数上。它通常用于处理 RESTful API 中的动态路径
     * @param id
     * @return
     */
    @GetMapping("/id")
    public R<DishDto> getById(@PathVariable Long id) {//参数名为 id，与路径中的 {id} 变量名匹配，因此变量值将被正确地绑定到方法参数上
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

//    /**
//     * 根据条件查询对应菜品数据
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
////        构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
////        添加条件，查询状态为1的（起售状态）
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
////        添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }


    /**
     * 根据条件查询对应菜品数据
     * 1. 新建 dishDto 对象
     * 2. 拷贝 dish 到 dishDto
     * 3. 获取 id(dishId)
     * 4. 根据 dishId 查口味 --- DishService 中的 getByIdWithFlavor
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) { // 新增套餐时的菜品查询
//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        添加条件，查询状态为1的（起售状态）
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
//        添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);


        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto>  dtoList = list.stream().map((item) -> { //item对应当前的菜品
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();

            //根据id查分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
//            当前菜品的 id,查询当前菜品对应的口味信息
//            等价于 --- DishService 中的 getByIdWithFlavor,所以可以直接调用这个方法
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            // SQL: select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorsList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorsList);
            return dishDto;
        }).collect(Collectors.toList());


        List<DishDto> dishDtoList = null;
        return R.success(dishDtoList);
    }
}
