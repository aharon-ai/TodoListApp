package com.example.todolist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;

public class TodoController {
    @FXML private ListView<TodoItem> todoListView;
    @FXML private TextField titleField;
    @FXML private DatePicker deadlinePicker;

    private ObservableList<TodoItem> todoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws IOException {
        // 1. Laden der alten Aufgaben aus der Datei
        FileHandler.loadTask(todoList);
        todoListView.setItems(todoList);
        // deadlinePicker.setValue(LocalDate.now());

        // definieren das Aussehen jeder Zeile
        todoListView.setCellFactory(lv -> new ListCell<TodoItem>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(TodoItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    // setText(null);
                    // setStyle(""); // Style zurücksetzen für leere Zellen
                } else {
                    checkBox.setText(item.getTitle() + " (bis: " + item.getDeadline() + ")");
                    checkBox.setSelected(item.isDone());

                    // Initialer Check beim Laden der Zelle
                    applyStrikethrough(checkBox.isSelected());

                    // WICHTIG: Speichern, wenn der Haken gesetzt wird
                    checkBox.setOnAction(event -> {
                        item.setDone(checkBox.isSelected());
                        try {
                            FileHandler.saveTasks(todoList); // Sofortiges Update beim Klick
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Status geändert: " + item.isDone());
                    });

                    setGraphic(checkBox);
                }
            }

            // Hilfsmethode für das Styling
            private void applyStrikethrough(boolean selected) {
                if (selected) {
                    // Wir nutzen Inline-CSS für den Text-Teil der CheckBox
                    //checkBox.setStyle("-fx-text-fill: gray;");
                    // JavaFX Hack: Strikethrough auf dem Label-Teil der Checkbox via CSS-Selektor
                    // Falls das nicht direkt greift, ist ein Label als Graphic oft besser.
                    //checkBox.lookup(".json").setStyle("-fx-strikethrough: true; "); // -fx-text-fill: gray;

                    checkBox.getStyleClass().add("done-task");

                } else {
//                    checkBox.setStyle("-fx-text-fill: black;");
//                    if (checkBox.lookup(".json") != null) {
//                        checkBox.lookup(".json").setStyle("-fx-strikethrough: false;-fx-text-fill: black;"); // -fx-text-fill: black;
                    checkBox.getStyleClass().remove("done-task");

                }
            }
        });
    }

    @FXML
    public void handleAddTodo() throws IOException {
        String title = titleField.getText();
        LocalDate date = deadlinePicker.getValue();

        if (!title.isEmpty()) {
            // Hier erstellen wir das Objekt. Wir nennen es 'item'
            TodoItem item = new TodoItem(title, "", date);

            //todoList.add(new TodoItem(title, "", date));

            todoList.add(item);
            FileHandler.saveTasks(todoList); // Speichern nach Hinzufügen
            titleField.clear();
        }
    }

    @FXML
    public void handleDeleteTodo() throws IOException {
        TodoItem selected = todoListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            todoList.remove(selected);
            FileHandler.saveTasks(todoList); // Speichern nach Löschen
        }
    }
}
