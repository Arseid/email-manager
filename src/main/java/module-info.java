module com.ynov.emailmanager {
    requires javafx.web;
    requires java.mail;
    requires javafx.fxml;
    requires jsoup;

    opens com.ynov.fx to javafx.fxml;
    exports com.ynov.fx;
}