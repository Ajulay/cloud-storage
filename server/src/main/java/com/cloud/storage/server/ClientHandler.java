package com.cloud.storage.server;

import com.cloud.storage.common.TransmitClass;

import java.io.*;
import java.net.Socket;

import static sun.jvm.hotspot.runtime.PerfMemory.start;

public class ClientHandler {
    Socket socket;
    Server server;
    public ClientHandler(Server server, Socket socket){
        this.socket = socket;
        start();
    }

private void start() {
  new Thread(new Runnable() {
        @Override
        public void run() {


            //welcome
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("\\connectok");
               // DataInputStream dis = new DataInputStream(socket.getInputStream());
              //  System.out.println(dis.readUTF());
//dos.close();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                TransmitClass tc = (TransmitClass) ois.readObject();

                File f = new File("/Users/ajulay/MyGit/cloud-storage/server/src/main/java/com/cloud/storage/tmpFiles" + tc.getFileName());
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f, true);
                fos.write(tc.getBuffer(), 1, tc.getBuffer().length-1);
                fos.flush();

while (true) break;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }).start();

    }
}


