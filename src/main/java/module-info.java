module com.example.todolist {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.google.gson;
    requires java.net.http;
    requires io.github.cdimascio.dotenv.java;

    opens com.example.todolist to javafx.fxml, com.google.gson;
    exports com.example.todolist;
}
