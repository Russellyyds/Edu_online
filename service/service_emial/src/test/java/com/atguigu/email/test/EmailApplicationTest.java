package com.atguigu.email.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailApplicationTest {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void test1() {
        Random randObj = new Random();
        String s = Integer.toString(100000 + randObj.nextInt(900000));
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject("简单邮件");
        mailMessage.setText("验证码"+s);
        mailMessage.setTo("954013384@qq.com");
        mailMessage.setFrom("954013384@qq.com");

        mailSender.send(mailMessage);
    }
}
