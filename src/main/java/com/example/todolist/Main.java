package com.example.todolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(javafx.stage.Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("todolist-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("Meine Todolist");
        stage.setScene(scene);
        stage.show();
    }

}
