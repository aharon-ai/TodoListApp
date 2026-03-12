module com.example.todolist {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.google.gson;
    requires java.net.http;

    opens com.example.todolist to javafx.fxml;
    exports com.example.todolist;
}
