package com.ynov.fx;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ComposeEmailController {

    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea messageField;

    public void sendEmail() {
        // Add the logic to send the email using the input from the text fields.
    }
}
