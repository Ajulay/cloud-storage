package com.cloud.storage.dao;

import java.sql.*;

public class SQLHandler {
    public static String URL = "jdbc:mysql://localhost:3306/dz?useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
    public static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    public static Connection conn;
    public static Statement stmt;

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER_NAME);
        conn = DriverManager.getConnection(URL, "root", "Ajulay");
       // stmt = conn.createStatement();
       // System.out.println("connect ok");
      //  disconnect();

    }

public static void addClientData(String name, long hashpass ) throws SQLException {
    try {
        stmt = conn.prepareStatement("INSERT INTO storage.clients(nickname, hashpass) VALUES (?,?);");
        ((PreparedStatement) stmt).setString(1, name);
        ((PreparedStatement) stmt).setLong(2, hashpass);
        ((PreparedStatement) stmt).execute();

    } catch (SQLException e) {
        e.printStackTrace();
        throw e;
    }
    finally {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public static String getClient(String name, long hashpass){
   String s = null;
    try {
        stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery("SELECT * FROM storage.clients WHERE nickname = '" + name + "' AND hashpass = " + hashpass + ";");
       if(rs.next()){
       s = rs.getString(2) + " " + rs.getLong(3);
       }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return s;
}









    public static void disconnect() throws SQLException {
        stmt.close();
        conn.close();
     //   System.out.println("disconnect ok");

    }


}
