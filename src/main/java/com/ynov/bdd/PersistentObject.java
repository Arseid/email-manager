package com.ynov.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class PersistentObject {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/emails";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public abstract void persist() throws SQLException, ClassNotFoundException;

    public static Connection dbConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }

}

