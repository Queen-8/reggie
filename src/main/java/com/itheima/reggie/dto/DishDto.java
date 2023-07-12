package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;


/**
 * dto: data transfer object 数据传输对象
 */
@Data
public class DishDto extends Dish {

    /**List<DishFlavor> flavors 接收下面的数组
     * 菜品对应的口味数据
     * flavors:[{name: "甜味", value: "["无糖","少糖","半糖","多糖","全糖"]", showOption: false}]
     * 0:{name: "甜味", value: "["无糖","少糖","半糖","多糖","全糖"]", showOption: false}
     */
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
