package com.cloud.storage.dao;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class SQLHandlerTest extends Object {

    @Test
    public void getClient() {
        try {
            SQLHandler.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(SQLHandler.getClient("First", 1));
        Assert.assertEquals("First 1", SQLHandler.getClient("First", 1));

    }
}