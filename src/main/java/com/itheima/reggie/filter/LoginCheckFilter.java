package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录【自定义的过滤器】
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*") // /*表示所有的请求都拦截
public class LoginCheckFilter implements Filter { //需要实现过滤器接口
    /**
     * PATH_MATCHER【方法名】: 路径匹配器，支持通配符写法
     * 如果遇到请求地址是：backend/index.html
     * 使用 AntPathMatcher 进行路径比较
     */

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        强制转型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1. 获取本次请求的url, 即 requestURL
        String requestURI = request.getRequestURI();
        log.info("LoginCheckFilter拦截到请求：{}",requestURI);    //{}表示一个占位符，可以输出后面的参数

//        定义不需要处理的请求路径
        String[] urls = new String[] {
                "/employee/login",   //过滤器应该直接放行，要登录【登录会请求login，要通过让用户登录，不能拦截】
                "/employee/logout",  //直接退出，不需要检查它是否登录【退出不拦截】
                "/backend/**", //重要控制页面数据，不是页面图片，所有这些图片可以不用拦截，直接放行【backend下的静态资源不拦截】
                "/front/**", //使用PATH_MATCHER:路径匹配器【front下的静态资源不拦截】
                "/common/**",//【运行common路径下的不拦截】
                "user/sendMsg",//移动端发送短信
                "user/login"//移动端登录
        };

//        2. 判断本次请求是否需要处理 -- 将不需要处理的路径与 requestURL 对比
        // 设置一个请求访问路径，如果访问到这个路径就需要处理 --- 说的，但没写
        boolean check = check(urls, requestURI);

//        3. 如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求不需要处理：{}",requestURI);
            filterChain.doFilter(request,response); //放行
            return;
        }

//        4-1. 判断登录状态，如果已经登录，则直接放行【网页的登录限制】
//        从 Session 里获取登录用户,当时登录的时候放的 "employee" ,现在还获取它
        if (request.getSession().getAttribute("employee") != null) {    //!= null 说明已经登录
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
//            获取用户id,并强转为Long型
            Long empId = (Long) request.getSession().getAttribute("employee");
//            有了上面的一句 下面的就不需要了
//            long id = Thread.currentThread().getId();
//            log.info("Login 线程id为：{}",id);

            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response); //放行
            return;
        }

//        4-2. 判断登录状态，如果已经登录，则直接放行【移动端的登录限制】
//        从 Session 里获取登录用户,当时登录的时候放的 "employee" ,现在还获取它
        if (request.getSession().getAttribute("user") != null) {    //!= null 说明已经登录
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));
//            获取用户id,并强转为Long型
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);   //BaseContext 通过ThreadLocal绑定用户id
            filterChain.doFilter(request,response); //放行
            return;
        }


//        5. 如果未登录则返回登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) { //检查 urls 中是否有一个可以跟 requestURI 进行匹配
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
