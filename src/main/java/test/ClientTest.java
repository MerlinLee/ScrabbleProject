package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
       bouncyCastleBase64();
    }

    public static void bouncyCastleBase64 () {
        byte[] encodeBytes = org.bouncycastle.util.encoders.Base64.encode("localhost".getBytes()) ;
        String encode = new String (encodeBytes);
        System.out.println("encode:  " + encode);

        byte[] decodeBytes = org.bouncycastle.util.encoders.Base64.decode(encode);
        String decode = new String(decodeBytes);
        System.out.println("decode:  " + decode);

    }
}
