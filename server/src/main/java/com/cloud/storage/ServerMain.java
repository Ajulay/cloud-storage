package com.cloud.storage;

import com.cloud.storage.dao.SQLHandler;
import com.cloud.storage.server.Server;

import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) {

        try {
            SQLHandler.connect();
            System.out.println("Test BD" + SQLHandler.getClient("First", 1) + "ok...");

        } catch (ClassNotFoundException e) {

            System.out.println("not class");
        } catch (SQLException e) {
            System.out.println("sql");

        }
      
        new Server().run();

    }
}
