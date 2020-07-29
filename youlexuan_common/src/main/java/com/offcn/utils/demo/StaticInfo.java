package com.offcn.utils.demo;

/**
 * 建议使用接口
 * 因为接口编译之后的字节码文件要比正常的javaclass要小很多
 * 所以理论上接口常量的加载速度要比类快很多
 * @author zhangjian
 * @email 13120082225@163.com
 * @date 2020/4/9
 */
public interface StaticInfo {
    String FDFSURL = "http://192.168.157.146/";
}
