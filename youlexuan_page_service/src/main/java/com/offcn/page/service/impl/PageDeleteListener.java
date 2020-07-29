package com.offcn.page.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.util.List;

public class PageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String source = textMessage.getText();
                List<Long> longs = JSON.parseArray(source, Long.class);
                if (itemPageService.deleteItemHtml(longs)) {
                    System.out.println("静态页面删除成功");
                } else {
                    System.out.println("静态页面删除失败");
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }

    }

}
