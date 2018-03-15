package com.lzwing.mail;

import com.lzwing.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: chenzhongyong@cecdat.com
 * Date: 2018/3/13
 * Time: 15:49
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Test
    public void testSendEmail() throws Exception{
        String toUser = "chenzhongyong@cecdat.com";
        emailService.sendSimpleMail(toUser,"测试内容。。。");
    }

    @Test
    public void testbalanceToken() {
        String emailToken = "12830ec2-9fcb-4174-8e0e-70b679dcf940";
        boolean b = emailService.balanceToken(emailToken);
        assertTrue(b);
    }

    @Test
    public void testgetEmailToken() {
        User user = new User();
        user.setName("zhangsan");
        user.setEmail("xxx@xx.com");
        String emailToken = emailService.getEmailToken(user);
        log.info(String.format("emailToken:%s", emailToken));
    }
}