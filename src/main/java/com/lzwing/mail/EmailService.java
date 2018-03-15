package com.lzwing.mail;

import com.lzwing.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service

public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String fromUser;

    public void sendHtmlMail(String to, String content) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        //true表示需要创建一个multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromUser);
        helper.setTo(to);
        helper.setSubject("主题：验证邮件");
        helper.setText(content, true);

        mailSender.send(message);
    }

    public void sendSimpleMail(String toUser, String content) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromUser);
        message.setTo(toUser);
        message.setSubject("主题：简单邮件");
        message.setText(content);
        mailSender.send(message);
    }

    public void senEmail(User user, String email) throws Exception {
        String emailToken = getEmailToken(user);
        String activeUrl = String.format("http://localhost:8899/activateMail?emailToken=%s", emailToken);
        String url = String.format("<a href='%s'>激活</a></br><h1>如果以上超连接无法访问，请将以下网址复制到浏览器地址栏中</h1><h2>%s</h2>", activeUrl, activeUrl);

        sendHtmlMail(fromUser, url);
    }

    public String getEmailToken(User user) {
        String token = UUID.randomUUID().toString();
        String value = user.toString();
        redisTemplate.opsForValue().set(token, value);
        redisTemplate.expire(token, 60, TimeUnit.SECONDS);
        return token;
    }

    public boolean balanceToken(String emailToken) {

        if (redisTemplate.opsForValue().get(emailToken) != null) {
            return true;
        }
        return false;
    }
}