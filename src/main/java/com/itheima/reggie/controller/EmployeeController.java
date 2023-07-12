package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController //在@Controller 基础上增加了 @ResponseBody 注解
@RequestMapping("/employee") //配置处理器的 HTTP 请求方法、url 等信息
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1. 将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2. 根据页面提交的用户名查询数据库
//        创建一个LambdaQueryWrapper对象，用于构建查询条件，mybatisplus提供
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        /**
         * 比较数据库表的"username"字段和从页面提交的用户名
         * Employee::getUsername是通过lambda表达式引用Employee类中的getUsername()方法，该方法返回数据库表中的"username"字段的值
         * employee.getUsername()是获取从页面提交的用户名的值
         */
        queryWrapper.eq(Employee::getUsername,employee.getUsername());

        /**
         * - 调用employeeService的getOne方法，将LambdaQueryWrapper对象作为参数传递进去。
         * getOne方法会根据查询条件查询数据库，并返回符合条件的第一条记录。
         * 这样，最终得到的emp对象就是数据库中与页面提交的用户名匹配的员工记录。
         * */
        Employee emp = employeeService.getOne(queryWrapper);

        //3. 如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        //4. 密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //5. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用状态");
        }

        //6. 登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        /**
         * 清理 Session 中保存的当前登录员工的id
         * 只需要从会话中删除键名为"employee"的属性
         */
        request.getSession().removeAttribute("employee"); //每个浏览器独占一个Session
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * //@RequestBody 提供 Restful API, 返回JSON数据格式
     * @RequestBody用于将HTTP请求的内容绑定到方法的参数上，用于接收客户端发送的数据。
     * @ResponseBody用于将方法的返回值序列化为HTTP响应的内容，用于向客户端发送数据。
     * @param employee
     * @return
     */
    @PostMapping    //本来请求路径是"/employee", 但在 @RequestMapping 注解里面加过了，可以不用重复
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
//        设置初始密码123456,需要md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        /**
         * 下面的本来是自动设置,但是可以使用自动填充字段 --- MyMetaObjecthandler类
         */
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")  // @RequestMapping("/employee") 在此基础上 + /page
    public R<Page> page(int page, int pageSize, String name) {
        /**
         * 1. Page 泛型里面包含 list,list 存放recoeds 的才是页面的数组
         * 2. page、pageSize 是点击分页时候提交的参数
         * 3. page、pageSize、name 是已经在分页页面进行搜索 name 时候提交的参数
         */
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
//        构造分页构造器 --- 第几页第几条
        Page pageInfo = new Page(page,pageSize);
//        构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        添加一个过滤条件：当这个name != null 时，添加进去
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName, name);
//        添加排序条件 -- 根据更新时间降序排
        queryWrapper.orderByDesc(Employee::getUpdateTime);
//        执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo); //重点构造page对象和queryWrapper对象
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("update employee 线程id为：{} ",id);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}