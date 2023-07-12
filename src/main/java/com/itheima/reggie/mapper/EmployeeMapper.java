package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper //Mapper 是数据持久层，给Mapper接口用来生成实体类，在编译之后自动生成相应接口的实体类
public interface EmployeeMapper extends BaseMapper<Employee> {
//    <Employee> 指定一个实体，在entity中的一个实体
}
