package com.cloud.storage.client;

import com.cloud.storage.common.TransmitClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Controller {
    SocketChannel sc;
    ByteBuffer bb;


   @FXML
    TableView<File> tableView;

   @FXML
    TableColumn<File, String> fileName;

   @FXML
   TableColumn<File, String> fileSize;

   @FXML
    ListView<String> listView;

   @FXML
    Button bnSend;//?why

    @FXML
    Button bnRemove;

    @FXML
    Button bnUpdate;

    @FXML
    Button bnLoadCl;//?why

    @FXML
    Button bnUpdateCl;

    @FXML
    Button bnRemoveCl;


   ObservableList<String> fileList;

   public void initialize(){
       fileList = FXCollections.observableArrayList();
       //fileList.addAll("Hello") test;
       listView.setItems(fileList);
        bnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Transmit...");
            }
        });
       bnRemove.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Remove here...");
            }
        });
       bnUpdate.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               System.out.println("Update from here");
           }
       });

       bnLoadCl.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               System.out.println("Load from THERE.");
           }
       }
       );

       bnUpdateCl.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               System.out.println("Update from THERE...");
           }
       });

       bnRemoveCl.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               System.out.println("Remove THERE..");
           }
       });
   }



    public void btnClickMeAction() {
        System.out.println("Java");
    }

    public void bnSendClick() {
        transmitAdd("/Users/ajulay/MyGit/cloud-storage/client/Hello.txt");
    }


    public void transmitAdd (String filename) {

        try {
           // FileReader fr = new FileReader(filename);


            Socket socket = new Socket("localhost", 3456);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
           // DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String s = dis.readUTF();
            System.out.println(s);
//dis.close();
            if (!s.equals("\\connectok")){
               throw new IOException();
               //
                // dos.writeUTF("Hello");
            }
            while(true){
                //autenthification
                break;
            }

            while (true){
                //working process
                File f = new File(filename);
                FileInputStream fis = new FileInputStream(filename);
                byte[] buffer = new byte[(int)f.length()+1];
                fis.read(buffer, 1, buffer.length-1);
                buffer[0] = 'o';
                TransmitClass tc = new TransmitClass(filename, buffer, 0);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(tc);

                System.out.println("Sent...");
                break;

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void transmitMove (String filename){

    }
    public void transmitDelete (String filename){

    }
    public void transmitCopy (String filename){

    }

    public void getChannel (){
        try {
            sc = SocketChannel.open(new InetSocketAddress("localhost", 3456));
            bb = ByteBuffer.allocate(60);

            while (sc.isConnected()){
                sc.read(bb);





            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
