package com.ynov.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmailManager {
    private final String owner;
    private final String appPassword;

    private Properties managerProperties;

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

    public void sendEmail (List<String> recipients, List<String> ccRecipients, List<String> bccRecipients,
                           String subject, String body, List<String> attachments) {
        // Authentification auprès du serveur
            Session session = Session.getInstance(managerProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(owner, appPassword);
            }
        });

        try {
            // Création du message à envoyer
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(owner));

            // Set recipients
            for (String recipient : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            // Set CC recipients
            for (String ccRecipient : ccRecipients) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
            }

            // Set BCC recipients
            for (String bccRecipient : bccRecipients) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccRecipient));
            }

            message.setSubject(subject);

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachments
            for (String filePath : attachments) {
                messageBodyPart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(filePath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(source.getName());
                multipart.addBodyPart(messageBodyPart);
            }

            // Set the message's content to the multipart
            message.setContent(multipart);

            // Send the message
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> readEmails() throws MessagingException {
        // Authentification auprès du serveur
        Session session = Session.getDefaultInstance(managerProperties);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", owner, appPassword);

        // Ouvre la boite des messages
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        // Récupère les messages
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
