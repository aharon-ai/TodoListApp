package com.example.todolist;

import com.example.todolist.model.TodoItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class TodoController {
    @FXML private ListView<TodoItem> todoListView;
    @FXML private TextField titleField;
    @FXML private DatePicker deadlinePicker;

    private ObservableList<TodoItem> todoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        todoListView.setItems(todoList);
        deadlinePicker.setValue(LocalDate.now());
    }

    @FXML
    public void handleAddTodo() {
        String title = titleField.getText();
        LocalDate date = deadlinePicker.getValue();

        if (!title.isEmpty()) {
            todoList.add(new TodoItem(title, "", date));
            titleField.clear();
        }
    }

    @FXML
    public void handleDeleteTodo() {
        TodoItem selected = todoListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            todoList.remove(selected);
        }
    }
}
