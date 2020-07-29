package com.offcn;


import org.apache.commons.io.IOUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;

public class Demo04 {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext_email.xml");

        JavaMailSenderImpl mailsend=(JavaMailSenderImpl) context.getBean("mailSender");

        MimeMessage mimemsg = mailsend.createMimeMessage();

        MimeMessageHelper help = new MimeMessageHelper(mimemsg, true, "GBK");

        help.setFrom("lyh2505818092@163.com");
        help.setTo("lyh2505818092@163.com");
        help.setSubject("html格式的邮件");
        //加载邮件模板
        String html= IOUtils.toString(new FileInputStream("template.txt"),"utf-8");
        //设定要发送的邮件内容,支持html
        help.setText(html, true);
        //添加html嵌入图片
        help.addInline("www", new File("F:\\图片\\2020.1.14李艺花   （张  周 郎敏） 4套\\李艺花 (59).jpg"));

        mailsend.send(mimemsg);

        System.out.println("send ok");
    }
}
