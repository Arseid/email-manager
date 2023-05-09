package com.ynov.email;

import com.ynov.bdd.PersistentObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email extends PersistentObject{
    private String subject;
    private String sender;
    private String body;
    private Date date;

    /**
     * Constructs an Email.
     *
     * @param subject The subject of the email.
     * @param sender The sender of the email.
     * @param body The body of the email.
     * @param date The date the email was sent.
     */
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

    /**
     * Checks if the email exists in the database.
     *
     * @return True if the email exists in the database, false otherwise.
     * @throws SQLException If there's an error while querying the database.
     */
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

    /**
     * Inserts the email into the database.
     *
     * @throws SQLException If there's an error while querying the database.
     */
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

    /**
     * Updates the email in the database.
     *
     * @throws SQLException If there's an error while querying the database.
     */
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

    /**
     * Persists the email in the database by updating or inserting it.
     *
     * @throws SQLException If there's an error while querying the database.
     */
    public void persist() throws SQLException {
        if (exists()) update();
        else insert();
    }

    /**
     * Fetches all emails from the database and returns them as a List.
     *
     * @return A List of Email.
     * @throws SQLException If there's an error while querying the database.
     */
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

    /**
     * Returns a String representation of the email, made up of the formatted date and the subject.
     *
     * @return A String representation of the email.
     */
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        String formattedDate = sdf.format(date);
        return formattedDate + " - " + subject;
    }
}
