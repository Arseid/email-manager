package com.ynov.fx;

import com.ynov.config.ConfigLoader;
import com.ynov.email.Email;
import com.ynov.email.EmailManager;
import com.ynov.email.ReadEmails;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    /**
     * Initializes the EmailController and sets up the application.
     * Connects to the email account, reads emails, and populates the TreeView with email data.
     * Sets up the WebView to display the selected email's content.
     */
    public void initialize(){
        updateStatusText("Connexion à la boite de réception");

        // Server properties
        Properties properties = ConfigLoader.loadConfig();

        // Owner's info
        String owner = properties.getProperty("owner");
        String appPassword = properties.getProperty("appPassword");

        EmailManager emailManager = new EmailManager(owner, appPassword, properties);

        updateStatusText("Mise à jour des emails");
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
        updateStatusText("Mise à jour terminée");

        // Create TreeView's root
        TreeItem<Object> root = new TreeItem<>("Boite de réception");

        // Fetch all emails in the database
        List<Email> emails;
        try {
            emails = fetchAll();
            Collections.reverse(emails);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Iteration on emails and new tree item for each browsed email
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

    /**
     * Opens the Compose Email window as a modal dialog.
     * Sets the current EmailController as the ComposeEmailController's EmailController.
     */
    @FXML
    private void openComposeEmail() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("compose-email.fxml"));
            Parent composeEmailParent = fxmlLoader.load();
            ComposeEmailController composeEmailController = fxmlLoader.getController();
            composeEmailController.setEmailController(this);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Composer un Email");
            stage.setScene(new Scene(composeEmailParent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a modal dialog of the context of the application.
     */
    @FXML
    private void showAboutWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));
            Parent aboutContent = loader.load();

            Dialog<Object> aboutDialog = new Dialog<>();
            aboutDialog.setTitle("A propos");
            aboutDialog.getDialogPane().setContent(aboutContent);
            aboutDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            aboutDialog.setResizable(true);
            aboutDialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the email list by removing the change listener, re-initializing the controller,
     * and re-attaching the change listener.
     */
    @FXML
    public void refreshAction() {
        viewEmailList.getSelectionModel().selectedItemProperty().removeListener(changeListener);
        initialize();
    }

    /**
     * Updates the status text with the text in parameter.
     *
     * @param newText The new text for the status bar.
     */
    public void updateStatusText(String newText) {
        statusText.setText(newText);
    }
}