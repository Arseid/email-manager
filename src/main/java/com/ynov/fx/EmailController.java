package com.ynov.fx;

import com.ynov.email.Email;
import com.ynov.email.EmailManager;
import com.ynov.email.ReadEmails;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.ynov.email.Email.fetchAll;

public class EmailController {

    private ChangeListener changeListener;

    @FXML
    private Label statusText;

    @FXML
    private TreeView viewEmailList;

    @FXML
    private WebView viewEmailContent;

    public void initialize(){
        // Serveur properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        // Informations du propriétaire
        String owner = "luminetruemain@gmail.com";
        String appPassword = "ycddltifbbamgmcm";

        EmailManager emailManager = new EmailManager(owner, appPassword, properties);

        try {
            // Lit et affiche les emails
            List<Message> messages = emailManager.readEmails();
            for (Message message : messages) {
                // System.out.println("---------------------------------");
                // System.out.println("Subject: " + message.getSubject());
                // System.out.println("From: " + message.getFrom()[0]);
                // System.out.println("Date: " + message.getSentDate());
                // System.out.println("Body: " + ReadEmails.getTextFromMessage(message));
                //emails.add(new Email(message.getSubject(), message.getFrom()[0].toString(),
                //        ReadEmails.getTextFromMessage(message), message.getSentDate()));
                Email email = new Email(message.getSubject(), message.getFrom()[0].toString(),
                        ReadEmails.getTextFromMessage(message), message.getSentDate());
                if (!email.exists()) {
                    email.persist();
                }
            }
            System.out.println(messages);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Création de la racine de mon TreeView
        TreeItem<Object> root = new TreeItem<>("Boite de réception");

        // Récupération de tous les emails en bdd
        List<Email> emails;
        try {
            emails = fetchAll();
            Collections.reverse(emails);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Itération sur les émails et nouveau tree item pour chaque email parcouru
        for (Email email: emails) {
            root.getChildren().add(new TreeItem<>(email));
        }

        // WebEngine webEngine = viewEmailContent.getEngine();
        // webEngine.loadContent("<h1>Salut Test</h1>");

        SelectionModel selectionModel = viewEmailList.getSelectionModel();
        changeListener = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                TreeItem<Email> selectedEmail = (TreeItem<Email>) newValue;
                if (selectedEmail.getValue() instanceof Email) {
                    Email email = selectedEmail.getValue();
                    WebEngine webEngine = viewEmailContent.getEngine();
                    String webContent = "Expéditeur : " + email.getSender() + "<br>" + email.getBody();
                    webEngine.loadContent(webContent);
                }
            }
        };
        selectionModel.selectedItemProperty().addListener(changeListener);

        viewEmailList.setRoot(root);
        root.setExpanded(true);
    }

    @FXML
    public void refreshAction() {
        viewEmailList.getSelectionModel().selectedItemProperty().removeListener(changeListener);
        initialize();
    }

    public void updateStatusText(String newText) {
        statusText.setText(newText);
    }
}