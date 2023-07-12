package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession session, @RequestBody User user) {
        log.info(user.toString());
//        获取邮箱号
        String email = user.getPhone();
        log.info("mail:{}",email);
        String subject = "瑞吉外卖";
//        StringUtils.isNotEmpty字符串非空判断
        if(StringUtils.isNotEmpty(email)) {
 //        生成 4 位的随机验证码,把验证码变成 String 类型
            String code = ValidateCodeUtils.generateValidateCode(4).toString();    //表示生成4位的验证码
            String text = "【瑞吉外卖】您好，您的登录验证码为：\" + code + \"，请尽快登录";
            log.info("验证码:{}",code);
//        发送短信
            userService.sendMsg(email, subject, text);
//        需要将我们生成的验证码保存到Session
            session.setAttribute(email, code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

//    移动端登录
    @PostMapping("/login")
//    Map 存 json 数据,Map 的 key --- phone ,value --- code
    public R<User> login(HttpSession session, @RequestBody Map map) {
        log.info(map.toString());
//        获取邮箱,用户输入的
        String phone = map.get("phone").toString();
        log.info("phone={}",phone);
//        获取验证码,用户输入的
        String code = map.get("code").toString();
        log.info("code={}",code);
//        获取 session 中保存的验证码
        Object sessionCode = session.getAttribute(phone);
//        如果 session 的验证码和用户输入的验证码进行比对, &&同时
        if(sessionCode != null && session.equals(code)) {
//            要是 User 数据库没有这个邮箱则自动注册,先看输入的邮箱是否存在数据库
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
//            获得唯一的用户,因为手机号是唯一的
            User user = userService.getOne(queryWrapper);
//            要是 User 数据库没有这个邮箱则自动注册
            if(user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
//            取邮箱的前五位为用户名
                user.setName(phone.substring(0,6));
                userService.save(user);
            }
//            不保存这个用户名就登不上去,因为过滤器需要得到这个 user 才放行,程序才知道你登录了
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
