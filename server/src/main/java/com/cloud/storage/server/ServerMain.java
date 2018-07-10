package com.cloud.storage.server;

import com.cloud.storage.server.dao.SQLHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) {

        try {
            SQLHandler.connect();
           // SQLHandler.addClientData("First", 1);
            System.out.println( SQLHandler.getClient("First", 1));

        } catch (ClassNotFoundException e) {

            System.out.println("not class");
        } catch (SQLException e) {
            System.out.println("sql");

        }
      //  new Server();
        new Server().start();

    }
}
