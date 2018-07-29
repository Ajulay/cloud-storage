package com.cloud.storage.common.nets;

import com.cloud.storage.common.TransmitClass;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class MyNet {
    private static Socket socket;
    private static ObjectEncoderOutputStream oeos;
    private static ObjectDecoderInputStream odos;




    private MyNet() {
    }

    public static synchronized Socket getSocketInstance() throws IOException {

        if(socket == null || socket.isClosed()){
            socket = new Socket("localhost", 3456);
        }

        return socket;
    }



    public static synchronized ObjectEncoderOutputStream getObjctEncodrInstance() throws IOException {
        if(oeos==null){
            oeos = new ObjectEncoderOutputStream(getSocketInstance().getOutputStream());
        }
        return oeos;
    }
    public static synchronized ObjectDecoderInputStream getObjctDecodrInstance() throws IOException {
        if(odos==null){
            odos = new ObjectDecoderInputStream(getSocketInstance().getInputStream());
        }
        return odos;
    }

    public static void sendData(TransmitClass tc) throws IOException {

            getObjctEncodrInstance().writeObject(tc);
            getObjctEncodrInstance().flush();

    }

    public static TransmitClass getTC() throws IOException, ClassNotFoundException {
     return   (TransmitClass)  getObjctDecodrInstance().readObject();
    }

    public static void close(){
        try {
            System.out.println("Socket close");
            oeos.close();
            odos.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
