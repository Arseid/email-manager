package com.ynov.email;

import com.ynov.bdd.PersistentObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email extends PersistentObject{
    private String subject;
    private String sender;
    private String body;
    private Date date;

    public Email(String subject, String sender, String body, Date date) {
        this.subject = subject;
        this.sender = sender;
        this.body = body;
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean exists() throws SQLException {
        Connection connection = dbConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM emails WHERE subject = ? AND sender = ? AND date = ?"
        );
        preparedStatement.setString(1, this.subject);
        preparedStatement.setString(2, this.sender);
        preparedStatement.setDate(3, new java.sql.Date(this.date.getTime()));
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        boolean exists = resultSet.getInt(1) > 0;
        preparedStatement.close();
        connection.close();
        return exists;
    }

    private void insert() throws SQLException {
        Connection connection = dbConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO emails (subject, sender, body, date) VALUES (?, ?, ?, ?)"
        );
        preparedStatement.setString(1, this.subject);
        preparedStatement.setString(2, this.sender);
        preparedStatement.setString(3, this.body);
        preparedStatement.setDate(4, new java.sql.Date(this.date.getTime()));
        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
    }

    private void update() throws SQLException {
        Connection connection = dbConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE emails SET subject = ?, sender = ?, body = ?, date = ?"
        );
        preparedStatement.setString(1, this.subject);
        preparedStatement.setString(2, this.sender);
        preparedStatement.setString(3, this.body);
        preparedStatement.setDate(4, new java.sql.Date(this.date.getTime()));
        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
    }

    public void persist() throws SQLException {
        if (exists()) update();
        else insert();
    }

    public static List<Email> fetchAll() throws SQLException {
        Connection connection = dbConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM emails"
        );

        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Email> emailsList = new ArrayList<>();

        while (resultSet.next()){
            emailsList.add(new Email(
                    resultSet.getString("subject"),
                    resultSet.getString("sender"),
                    resultSet.getString("body"),
                    resultSet.getDate("date")
            ));
        }

        preparedStatement.close();
        connection.close();

        return emailsList;
    }

    @Override
    public String toString() {
        return "[+"+date+"] - "+subject;
    }
}
