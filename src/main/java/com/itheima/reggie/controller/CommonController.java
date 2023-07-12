package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        /**
         * Spring 框架在 spring-web 包中对文件上传进行了封装，大大简化了服务端代码，
         * 只需要在 Controller 的方法中声明一个 MultipartFile类型的参数即可接收上传的文件
         * file 是一个临时文件，需要转存(transferTo)到指定位置，否则本次请求完成后临时文件会删除
         */
        log.info(file.toString());

        /**
         * 直接使用原始文件名作为上传后的图片名字，但是这样如果有重名不能处理，所以下面正确的采用随机生成文件名入手
         * String originalFilename = file.getOriginalFilename(); //获取原始文件名
         *         try {
         *             //将临时文件转存到指定位置,转存到的位置可以通过配置文件的方式指定,
         *             basePath + originalFilename = 路径 + 文件名
         *             file.transferTo(new File(basePath + originalFilename));
         *         } catch (IOException e) {
         *             e.printStackTrace();
         *          }
         */

        String originalFilename = file.getOriginalFilename();   //获取原始文件名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));  //获取原始文件名的后缀
        //使用 UUID 重新生成文件名，防止文件名称重复，造成文件覆盖，文件名+后缀
        String fineName = UUID.randomUUID().toString() + suffix; //注意文件名的后缀，如：abx.jpg
        File dir = new File(basePath);  //创建一个目录对象
        if(!dir.exists()) { //判断当前目录是否存在
            dir.mkdirs();   //目录不存在，需要创建
        }

        try {
            //将文件转存到指定位置
            file.transferTo(new File(basePath + fineName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 上传完之后需要给页面返回文件名，因为新增菜品产生的名字，需要保存在菜品表里
         */
        return R.success(fineName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpg");

            int len = 0;
            byte[] bytes = new byte[1024];
            /**
             * 输入流读取的内容存在bytes，并将其长度赋值给 len
             * 如果 len != -1 说明没有读完
             */
            while( (len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();   //刷新
            }

            // 关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) { //输入和输出流的异常都通过Exception e 捕获这个大的异常来实现
            throw new RuntimeException(e);
        }

    }
}
