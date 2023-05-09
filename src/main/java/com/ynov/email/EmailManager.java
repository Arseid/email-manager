package com.ynov.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmailManager {
    private final String owner;
    private final String appPassword;

    private Properties managerProperties;

    /**
     * Constructs an EmailManager.
     *
     * @param owner The email address of the owner.
     * @param appPassword The application password of the owner's email.
     * @param managerProperties The properties for the email manager.
     */
    public EmailManager(String owner, String appPassword, Properties managerProperties) {
        this.owner = owner;
        this.appPassword = appPassword;
        this.managerProperties = managerProperties;
    }

    public String getOwner() {
        return owner;
    }

    public String getAppPassword() {
        return appPassword;
    }

    public Properties getManagerProperties() {
        return managerProperties;
    }

    public void setManagerProperties(Properties managerProperties) {
        this.managerProperties = managerProperties;
    }

    /**
     * Sends an email. The CC/BCC recipients and attachments are optional.
     *
     * @param recipients The list of recipients.
     * @param ccRecipients The list of CC recipients.
     * @param bccRecipients The list of BCC recipients.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @param attachments The list of attachments.
     */
    public void sendEmail (List<String> recipients, List<String> ccRecipients, List<String> bccRecipients,
                           String subject, String body, List<File> attachments) {
        // Log in the server
            Session session = Session.getInstance(managerProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(owner, appPassword);
            }
        });

        try {
            // Create message to send
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(owner));

            // Set recipients
            for (String recipient : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            // Set CC recipients
            if (ccRecipients != null) {
                for (String ccRecipient : ccRecipients) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
                }
            }

            // Set BCC recipients
            if (bccRecipients != null) {
                for (String bccRecipient : bccRecipients) {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccRecipient));
                }
            }

            message.setSubject(subject);

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachments
            if (attachments != null) {
                for (File filePath : attachments) {
                    messageBodyPart = new MimeBodyPart();
                    FileDataSource source = new FileDataSource(filePath);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(source.getName());
                    multipart.addBodyPart(messageBodyPart);
                }
            }

            // Set the message's content to the multipart
            message.setContent(multipart);

            // Send the message
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads emails from the inbox and returns them as a list of Message.
     *
     * @return A list of Message from the inbox.
     * @throws MessagingException If there's an error while reading emails.
     */
    public List<Message> readEmails() throws MessagingException {
        // Log in the server
        Session session = Session.getDefaultInstance(managerProperties);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", owner, appPassword);

        // Opens the inbox
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        // Fetch messages
        List<Message> messages = new ArrayList<>();
        for (Message message : inbox.getMessages()) {

            messages.add(message);
        }

        List<Message> listMessages = messages;

         // Ferme la boite
         // inbox.close(false);
         // store.close();

        return listMessages;
    }
}
