package com.offcn;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test1 {
    public static void main(String[] args) throws Exception{
       //createHtml();
     //  createJava();
       FtlDemo();
    }

    private static void FtlDemo()throws Exception {
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在目录
        configuration.setDirectoryForTemplateLoading(new File("F:\\youlexuan_parent\\youlexuan_freemarkerdemo\\src\\main\\resources"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("ftldemo.ftl");
        //创建数据模型
        Map map=new HashMap();
        List goodsList=new ArrayList();
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);
        map.put("goodsList", goodsList);


       // map.put("success", false);
       // map.put("username", "Person");
        //创建Writer对象
        FileWriter out = new FileWriter(new File("F:\\youlexuan_parent\\youlexuan_freemarkerdemo\\src\\main\\resources\\FtlDemo.html"));
        //输出
        template.process(map, out);
        System.out.println("生成成功");
        //关闭输出流
        out.close();
    }

    private static void createJava() throws Exception{
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在目录
        configuration.setDirectoryForTemplateLoading(new File("F:\\youlexuan_parent\\youlexuan_freemarkerdemo\\src\\main\\resources"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("clazz.ftl");
        //创建数据模型
        Map map=new HashMap();
        map.put("clazzName", "Person");
        map.put("printInfos", "欢迎来优就业学习！");
        //创建Writer对象
        FileWriter out = new FileWriter(new File("F:\\youlexuan_parent\\youlexuan_freemarkerdemo\\src\\main\\resources\\Person.java"));
        //输出
        template.process(map, out);
        System.out.println("生成成功");
        //关闭输出流
        out.close();
    }

    private static void createHtml() throws Exception {
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在目录
        configuration.setDirectoryForTemplateLoading(new File("F:\\youlexuan_parent\\youlexuan_freemarkerdemo\\src\\main\\resources"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("test.ftl");
        //创建数据模型
        Map map=new HashMap();
        map.put("name", "学员");
        map.put("message", "欢迎来优就业学习！");
        //创建Writer对象
        FileWriter out = new FileWriter(new File("F:\\youlexuan_parent\\youlexuan_freemarkerdemo\\src\\main\\resources\\demo.html"));
        //输出
        template.process(map, out);
        System.out.println("生成成功");
        //关闭输出流
        out.close();
    }
}
