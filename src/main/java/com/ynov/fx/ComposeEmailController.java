package com.ynov.fx;

import com.ynov.email.EmailManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ComposeEmailController {

    @FXML
    private TextField recipient;

    @FXML
    private TextField subject;

    @FXML
    private TextArea message;

    private EmailController emailController;

    public void setEmailController(EmailController emailController) {
        this.emailController = emailController;
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
        emailManager.sendEmail(recipients, subject.getText(), message.getText());

        // Update status bar
        if (emailController != null) {
            emailController.updateStatusText("Email envoyé");
        }

        // Close the window
        Stage stage = (Stage) recipient.getScene().getWindow();
        stage.close();
    }
}
