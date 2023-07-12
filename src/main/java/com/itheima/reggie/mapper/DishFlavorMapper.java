package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;


//继承了接口 BaseMapper，这样就可以进行单表增删改查
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {

}
