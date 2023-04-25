package com.ynov.email;

import java.util.Date;

public class Email {
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

    @Override
    public String toString() {
        return subject;
    }
}
