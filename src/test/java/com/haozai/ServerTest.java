package com.haozai;

import org.junit.Test;

/**
 * @author jackliu  Email:
 * @description: 字体测试类
 * @Version
 * @create 2023-07-17 10:06
 */
public class ServerTest {
    public static final String reset = "\u001B[0m";  // 重置所有属性
    public static final String white = "\u001B[30m"; // 白色字体
    public static final String red = "\u001B[31m";   // 红色字体
    public static final String yellow = "\u001B[32m"; // 黄色字体
    public static final String blue = "\u001B[34m";  // 蓝色字体
    public static final String purple = "\u001B[35m";// 紫色字体
    public static final String cyan = "\u001B[36m";  // 青色字体

    @Test
    public void  test1(){

        System.out.println(cyan+"this is test");

    }

    @Test
    public void test2(){
        String select = "session -i 2";
        if ( select.contains("session") && select.contains("-i")){
            //需要切换到指定的会话
            //session -i 1 //选择第一个
            String s = select.replaceAll(" ", "");
            int pos = s.indexOf("-i");
            System.out.println(select.substring(pos+3,select.length()));
        }
    }

    public static void main(String[] args) {

    }

}
