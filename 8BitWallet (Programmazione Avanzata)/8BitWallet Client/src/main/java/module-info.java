module progetto665406.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires com.google.gson;
    requires java.base;

    opens progetto665406.client to javafx.fxml;
    exports progetto665406.client;
}
