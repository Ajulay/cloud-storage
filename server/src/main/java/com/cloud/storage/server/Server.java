package com.cloud.storage.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
   // private Vector<String> cl
    ServerSocketChannel ssc;
    ByteBuffer bb ;
    Selector selector;




    public Server() {

        {
            try {
                ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(3456));
                bb = ByteBuffer.allocate(60);
                ssc.configureBlocking(false);
                selector = Selector.open();

                ssc.register(selector, SelectionKey.OP_ACCEPT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



       /*  try {
             ServerSocket serverSocket = new ServerSocket(3456);
             while (true) {
                 Socket sc = serverSocket.accept();
                new ClientHandler(this, sc);
             }

         } catch (IOException e) {
             e.printStackTrace();
         }
     */
    }

     public void start(){

         try {
             Iterator<SelectionKey> it;
             SelectionKey sk;
             while (ssc.isOpen()){
                // SocketChannel sc = ssc.accept();
                 selector.select();
                  it = selector.selectedKeys().iterator();
                 // it.remove();
                while (it.hasNext()){
                   sk = it.next();
                   // System.out.println(1);
                   it.remove();
                    if (sk.isAcceptable()) accept(sk);
                    if (sk.isReadable()) transmit(sk);

                }



             }



         } catch (IOException e) {
             e.printStackTrace();
         }


     }

     public void accept(SelectionKey sk){
         try {
             SocketChannel sc = ((ServerSocketChannel)sk.channel()).accept();
             String address = (new StringBuilder(sc.socket().getInetAddress().toString())).append(":").append(sc.socket().getPort()).toString();
             sc.configureBlocking(false);
             sc.register(selector, SelectionKey.OP_READ, address);
            // sc.register(selector, SelectionKey.OP_READ);
             bb.put("Hello!!!".getBytes());
             bb.flip();
             sc.write(bb);
         } catch (IOException e) {
             e.printStackTrace();
         }
         System.out.println("Accept");
     }

     public void transmit(SelectionKey sk){
         System.out.println("Read");
         SocketChannel ch = (SocketChannel) sk.channel();
         try {
             ch.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
}
