package com.offcn.page.service.impl;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            System.out.println("接收到消息：" + text);
           // boolean b = itemPageService.genItemHtml(Long.parseLong(text));
        if(itemPageService.genItemHtml(Long.parseLong(text))){
            System.out.println("静态页面生成成功");
        }else{
            System.out.println("静态页面生成失败");
        }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
