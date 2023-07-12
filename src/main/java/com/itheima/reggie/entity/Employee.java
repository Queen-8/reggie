package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.File;
import java.io.FileFilter;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data //提供综合的生成代码功能(@Getter + @Setter + @ToString + @EqualsAndHashCode)
public class Employee implements Serializable {
//    哪些属性是公共字段,就在属性上加注解 @TableField

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber; //身份证号

    private Integer status;

    //@TableField 公共字段自动填充
    @TableField(fill = FieldFill.INSERT)    //插入时填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;   //插入和更新时填充字段

    @TableField(fill = FieldFill.INSERT)    //插入时填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private Long updateUser;

}
