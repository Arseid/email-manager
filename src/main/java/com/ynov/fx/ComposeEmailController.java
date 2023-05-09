package com.ynov.fx;

import com.ynov.email.EmailManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ComposeEmailController {

    @FXML
    private TextField recipient;

    @FXML
    private TextField cc;

    @FXML
    private TextField bcc;

    @FXML
    private TextField subject;

    @FXML
    private TextArea message;

    @FXML
    private TextField attachments;

    private EmailController emailController;

    private List<String> attachmentPaths = new ArrayList<>();

    public void setEmailController(EmailController emailController) {
        this.emailController = emailController;
    }

    @FXML
    private void addAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an attachment");
        File file = fileChooser.showOpenDialog(recipient.getScene().getWindow());

        if (file != null) {
            attachmentPaths.add(file.getAbsolutePath());
            attachments.setText(attachments.getText() + (attachments.getText().isEmpty() ? "" : ", ") + file.getName());
        }
    }

    public void sendEmail() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        // Parse recipients from the input field
        List<String> recipients = Arrays.asList(recipient.getText().split("[;,]\\s*"));

        EmailManager emailManager = new EmailManager("luminetruemain@gmail.com", "ycddltifbbamgmcm", properties);

        // Check if the CC field is empty
        List<String> ccRecipients = cc.getText().isEmpty() ? null : Arrays.asList(cc.getText().split("[;,]\\s*"));

        // Check if the BCC field is empty
        List<String> bccRecipients = bcc.getText().isEmpty() ? null : Arrays.asList(bcc.getText().split("[;,]\\s*"));

        // Check if there are any attachments
        List<File> attachmentsList = attachmentPaths.isEmpty() ? null : new ArrayList<>();
        for (String path : attachmentPaths) {
            attachmentsList.add(new File(path));
        }

        emailManager.sendEmail(recipients, ccRecipients, bccRecipients, subject.getText(), message.getText(), attachmentsList);

        // Update status bar
        if (emailController != null) {
            emailController.updateStatusText("Email envoyé");
        }

        // Close the window
        Stage stage = (Stage) recipient.getScene().getWindow();
        stage.close();
    }
}
