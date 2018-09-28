package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);
        try {
            Socket socket = new Socket("localhost", 6666);
            //开启一个线程接收信息，并解析
            ClientThread thread=new ClientThread(socket);
            thread.setName("Client1");
            thread.start();
            //主线程用来发送信息
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out=new PrintWriter(socket.getOutputStream());
            while(true)
            {
                String s=read.nextLine();
                out.println(s);
                out.flush();
            }
        }catch(Exception e){
            System.out.println("服务器异常");
        }
    }
}
