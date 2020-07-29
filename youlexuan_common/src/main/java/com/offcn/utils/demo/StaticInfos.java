package com.offcn.utils.demo;

/**
 * 理论上 final修饰的类不能够被继承  比如String
 * 另外,final修饰的类其运行速度要比常规的类要快很多，因为final关键字能够绕开虚拟机的一些相关检测
 * @author zhangjian
 * @email 13120082225@163.com
 * @date 2020/4/9
 */
public final class StaticInfos {
    /** fdfs文件系统主机地址 */
    public static final String FDFSSERVERURL="http://192.168.116.146/";
}
