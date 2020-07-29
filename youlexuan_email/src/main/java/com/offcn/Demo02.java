package com.offcn;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

public class Demo02 {
    public static void main(String[] args) throws MessagingException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext_email.xml");

        JavaMailSenderImpl mailsend = (JavaMailSenderImpl) context.getBean("mailSender");
        MimeMessage mimeMessage = mailsend.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("lyh2505818092@163.com");
        helper.setTo("lyh2505818092@163.com");
        helper.setSubject("带附件的简单邮件");
        helper.setText("这是一封带附件的简单邮件,正文");

        File f1 = new File("D:\\Documents\\Desktop\\第四阶段总结.txt");
        File f2 = new File("D:\\Documents\\Desktop\\settings.xml");

        helper.addAttachment("第四阶段总结.txt", f1);
        helper.addAttachment("settings.xml", f2);

        mailsend.send(mimeMessage);
        //发送邮件

        //  mailsend.send(msg);

        System.out.println("send ok");

    }
}
