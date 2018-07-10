package com.cloud.storage.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static java.lang.Thread.sleep;

public class AnotherClient {
    SocketChannel sc;
    int port = 3456;

    public AnotherClient() {
        try {
            this.sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 3456));
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.port = port;
    }

    public void start() throws UnsupportedEncodingException {
        ByteBuffer bb = ByteBuffer.allocate(19);
        bb.put("It's me...\n".getBytes("utf-8"));
          // bb.clear();
            bb.flip();
          //  bb.clear();
        try {
           sc.write(bb);
         //  bb.clear();


//sleep(5000);

        //  sc.read(bb);
           // bb.flip();
         // sc.read(bb);
         //   bb.flip();
        //    StringBuilder sb = new StringBuilder();
         //   while (bb.hasRemaining()){
           //     System.out.print((char)bb.get());
          //  }
          //  System.out.println();
           /* int bytesRead = sc.read(bb);
            while (bytesRead != -1) {

                System.out.println("Read " + bytesRead);
                bb.flip();

                while(bb.hasRemaining()){
                    System.out.print((char) bb.get());
                }

                bb.clear();
                bytesRead = sc.read(bb);
            }*/

            //bb.flip();
          //  bb.put("Hello2".getBytes());
           // sc.write(bb);
           // bb.clear();
           // bb.flip();


          //  sc.write(bb);
          //  bb.flip();
           // sc.read(bb);

          //  System.out.println(new String(bb.array()));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new AnotherClient().start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
