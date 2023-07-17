package com.haozai.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 *主线程负责等待客户端的连接
 * 其他线程负责和客户端通信
 *
 * @author jackliu  Email:
 * @description: 服务端代码
 * @Version
 * @create 2023-07-17 9:59
 */
public class ServerC {

    public static final String reset = "\u001B[0m";  // 重置所有属性
    public static final String white = "\u001B[30m"; // 白色字体
    public static final String red = "\u001B[31m";   // 红色字体
    public static final String yellow = "\u001B[32m"; // 黄色字体
    public static final String blue = "\u001B[34m";  // 蓝色字体
    public static final String purple = "\u001B[35m";// 紫色字体
    public static final String cyan = "\u001B[36m";  // 青色字体

    //接收客户端的连接
    private static Socket clientSocket = null;

    private static  ServerSocket serverSocket = null;


    public static void main(String[] args) {



        Scanner scanner = new Scanner(System.in);
        ServerThread.socketList.add(null);

        try {
            serverSocket = new ServerSocket(4455);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }



            welcome();
            System.out.println(cyan + "等待客户端上线...." + reset);
            //开启监听
            int oneTimes = 0;
            try {
                while (true){

                    clientSocket = serverSocket.accept();
                    oneTimes ++;
                    System.out.println(blue + clientSocket.getInetAddress()+" client Online" + reset);
                    new ServerThread(clientSocket).start();
                    if (oneTimes == 1){
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                ServerThread.socketMapName.put(0,Thread.currentThread());
                                while (true) {
                                    System.out.print("c&c>>>");
                                    String cmd = scanner.nextLine();
                                    try {
                                        menu(cmd);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                }
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }

    public static void menu(String select ) throws InterruptedException {

        select = select.trim();

        int choice;
        if ( select.contains("session") && select.contains("-i")){
            //需要切换到指定的会话
            //session -i 1 //选择第一个
            String s = select.replaceAll(" ", "");
            int pos = s.indexOf("-i");
            choice = Integer.parseInt(select.substring(pos+3,select.length()).trim());
            runT(choice);
        }

        switch (select){

            case "session":
                showSession();
                break;
            case "help":
                showHelp();
            default:
                System.out.println(red + "正在coding ...." + reset);
                break;
        }

    }

    private static void showHelp() {
        System.out.println(blue+ "=========help==========="  );
        System.out.println("    session [-i id]  获取当前客户端的个数[连接对应的id客户端]" + reset  );
    }

    private static void runT(int i)  {
        synchronized (ServerC.class){

            ServerThread st = (ServerThread) ServerThread.socketMapName.get(i);
            st.interrupt();
            //阻塞当前进程
            try {
                ServerC.class.wait();
            } catch (InterruptedException e) {
                System.out.println("切换到主线程成功！" + reset);
            }

        }

    }


    public static void welcome(){
        System.out.println(yellow+"====Welcome====" + reset);
        System.out.println(purple+"usage:"     );
        System.out.println(purple+"        --help 帮助文档");
        System.out.println(purple+"        --exit 退出" + reset);
    }
    private static  void showSession() {

        System.out.println("当前客户端数量:");
        System.out.println(purple + "id ======  ip =======port" + reset);
        for (int i = 1; i < ServerThread.socketList.size(); i++) {
            System.out.println( yellow + i +" ======  "+ ServerThread.socketList.get(i).getInetAddress() +" =======" + ServerThread.socketList.get(i).getPort() +reset );
        }
        System.out.println();
    }

}
