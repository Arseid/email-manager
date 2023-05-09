package com.ynov.fx;

import com.ynov.email.Email;
import com.ynov.email.EmailManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Properties;

public class ComposeEmailController {

    @FXML
    private TextField recipient;

    @FXML
    private TextField subject;

    @FXML
    private TextArea message;

    public void sendEmail() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        EmailManager emailManager = new EmailManager("luminetruemain@gmail.com", "ycddltifbbamgmcm", properties);
        emailManager.sendEmail(recipient.getText(), subject.getText(), message.getText());

        // Close the window
        Stage stage = (Stage) recipient.getScene().getWindow();
        stage.close();
    }
}
