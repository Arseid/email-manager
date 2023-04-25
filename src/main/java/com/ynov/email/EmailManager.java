package com.ynov.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

    public void sendEmail (String receiver, String subject, String body) {
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            message.setSubject(subject);
            message.setText(body);

            // Envoie du message
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

        // Ferme la boite
        // inbox.close(false);
        // store.close();

        return messages;
    }
}
