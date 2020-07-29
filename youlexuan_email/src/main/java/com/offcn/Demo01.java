package com.offcn;


import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class Demo01 {
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext_email.xml");

        JavaMailSenderImpl mailsend=(JavaMailSenderImpl) context.getBean("mailSender");

        //创建简单的邮件
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("lyh2505818092@163.com");
        msg.setTo("lyh2505818092@163.com");
        msg.setSubject("JAVA0115测试邮件");
        msg.setText("好好学习,天天向上!");

        //发送邮件

        mailsend.send(msg);

        System.out.println("send ok");
    }
}
