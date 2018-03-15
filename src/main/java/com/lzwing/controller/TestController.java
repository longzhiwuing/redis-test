package com.lzwing.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.lzwing.domain.User;
import com.lzwing.mail.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: chenzhongyong@cecdat.com
 * Date: 2018/3/14
 * Time: 16:43
 */
@Controller
@Slf4j
public class TestController {

    @Autowired
    EmailService emailService;

    @Autowired
    DefaultKaptcha captchaProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "test")
    public String test() {
        return "test";
    }


    @RequestMapping(value = "myajaxRegister")
    @ResponseBody
    public String sendEmail(@RequestParam String email) throws Exception{
        User user = new User();
        new Thread(){
            @Override
            public void run(){
                try {
                    emailService.senEmail(user,email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return "邮件已发送至您的邮箱，请激活";
    }

    @RequestMapping(value = "activateMail")
    @ResponseBody
    public String activateMail(@RequestParam String emailToken){
        log.info(String.format("emailToken:%s", emailToken));
        if (emailService.balanceToken(emailToken)){
            return "success";
        }
        return "error1";
    }

    @RequestMapping(value = "/captcha-image")
    public ModelAndView getKaptchaImage(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control",
                "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText = captchaProducer.createText();
        System.out.println("capText: " + capText);

        try {
            String uuid= UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(uuid, capText,60*5, TimeUnit.SECONDS);
            Cookie cookie = new Cookie("captchaCode",uuid);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }



        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }
}
