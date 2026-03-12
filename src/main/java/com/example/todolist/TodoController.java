package com.example.todolist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class TodoController {
    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private DatePicker deadlinePicker;

    private final ObservableList<TodoItem> todoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        deadlinePicker.setValue(LocalDate.now());
        todoListView.setItems(todoList);
        todoListView.setCellFactory(listView -> new TodoItemCell());
        try {
            todoList.addAll(SupabaseClient.fetchTodos());
        } catch (IllegalStateException e) {
            showAlert("Supabase nicht konfiguriert", e.getMessage(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Laden fehlgeschlagen", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAddTodo() {
        String title = titleField.getText().trim();
        String description = Optional.ofNullable(descriptionField.getText()).orElse("").trim();
        LocalDate deadline = Optional.ofNullable(deadlinePicker.getValue()).orElse(LocalDate.now());

        if (title.isEmpty()) {
            showAlert("Titel fehlt", "Bitte gib der Aufgabe einen Titel.", Alert.AlertType.WARNING);
            return;
        }

        TodoItem newItem = new TodoItem(title, description, deadline);
        try {
            TodoItem created = SupabaseClient.createTodo(newItem);
            todoList.add(created);
            titleField.clear();
            descriptionField.clear();
            deadlinePicker.setValue(LocalDate.now());
        } catch (IllegalStateException e) {
            showAlert("Supabase nicht konfiguriert", e.getMessage(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Fehler beim Speichern", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshView() {
        todoListView.refresh();
    }

    private void showAlert(String header, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class TodoItemCell extends ListCell<TodoItem> {
        private final HBox root = new HBox(10);
        private final Label idLabel = new Label();
        private final CheckBox doneCheck = new CheckBox();
        private final VBox textBox = new VBox(4);
        private final Label titleLabel = new Label();
        private final Label metaLabel = new Label();
        private final Button editButton = new Button("Bearbeiten");
        private final Button deleteButton = new Button("Löschen");

        private TodoItemCell() {
            HBox buttonBox = new HBox(6, editButton, deleteButton);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            textBox.getChildren().addAll(titleLabel, metaLabel);
            textBox.setMaxWidth(Double.MAX_VALUE);

            HBox.setHgrow(textBox, Priority.ALWAYS);
            root.getChildren().addAll(idLabel, doneCheck, textBox, buttonBox);
            root.setAlignment(Pos.CENTER_LEFT);
            root.getStyleClass().add("todo-cell");

            doneCheck.setOnAction(event -> {
                TodoItem item = getItem();
                if (item != null) {
                    toggleDone(item, doneCheck.isSelected());
                }
            });
            deleteButton.setOnAction(event -> deleteCurrentItem());
            editButton.setOnAction(event -> openEditor());
        }

        @Override
        protected void updateItem(TodoItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                idLabel.setText("#" + (item.getId() == null ? "neu" : item.getId()));
                titleLabel.setText(item.getTitle());
                metaLabel.setText(buildMeta(item));
                doneCheck.setSelected(item.isDone());
                applyDoneStyle(item.isDone());
                setGraphic(root);
            }
        }

        private String buildMeta(TodoItem item) {
            StringBuilder builder = new StringBuilder("Fällig: ").append(item.getDeadline());
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                builder.append(" • ").append(item.getDescription());
            }
            return builder.toString();
        }

        private void applyDoneStyle(boolean done) {
            String titleStyle = done ? "-fx-strikethrough: true; -fx-text-fill: #888;" : "-fx-strikethrough: false; -fx-text-fill: black;";
            String metaStyle = done ? "-fx-text-fill: #aaa;" : "-fx-text-fill: #555;";
            titleLabel.setStyle(titleStyle);
            metaLabel.setStyle(metaStyle);
        }

        private void toggleDone(TodoItem item, boolean completed) {
            item.setDone(completed);
            try {
                TodoItem updated = SupabaseClient.updateTodo(item);
                item.setTitle(updated.getTitle());
                item.setDescription(updated.getDescription());
                item.setDeadline(updated.getDeadline());
                item.setDone(updated.isDone());
                applyDoneStyle(updated.isDone());
                refreshView();
            } catch (IllegalStateException e) {
                showAlert("Supabase nicht konfiguriert", e.getMessage(), Alert.AlertType.INFORMATION);
                resetDoneToggle(item, !completed);
            } catch (IOException e) {
                showAlert("Fehler beim Aktualisieren", e.getMessage(), Alert.AlertType.ERROR);
                resetDoneToggle(item, !completed);
            }
        }

        private void resetDoneToggle(TodoItem item, boolean state) {
            item.setDone(state);
            doneCheck.setSelected(state);
        }

        private void deleteCurrentItem() {
            TodoItem item = getItem();
            if (item == null) {
                return;
            }
            try {
                SupabaseClient.deleteTodo(item);
                todoList.remove(item);
            } catch (IllegalStateException e) {
                showAlert("Supabase nicht konfiguriert", e.getMessage(), Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                showAlert("Fehler beim Löschen", e.getMessage(), Alert.AlertType.ERROR);
            }
        }

        private void openEditor() {
            TodoItem item = getItem();
            if (item == null) {
                return;
            }
            TodoEditDialog dialog = new TodoEditDialog(item);
            Optional<TodoItem> result = dialog.showAndWait();
            result.ifPresent(updated -> {
                try {
                    TodoItem saved = SupabaseClient.updateTodo(updated);
                    item.setTitle(saved.getTitle());
                    item.setDescription(saved.getDescription());
                    item.setDeadline(saved.getDeadline());
                    item.setDone(saved.isDone());
                    applyDoneStyle(saved.isDone());
                    refreshView();
                } catch (IllegalStateException e) {
                    showAlert("Supabase nicht konfiguriert", e.getMessage(), Alert.AlertType.INFORMATION);
                } catch (IOException e) {
                    showAlert("Fehler beim Speichern", e.getMessage(), Alert.AlertType.ERROR);
                }
            });
        }
    }

    private class TodoEditDialog extends Dialog<TodoItem> {
        private final TextField titleInput = new TextField();
        private final TextArea descriptionInput = new TextArea();
        private final DatePicker datePicker = new DatePicker();

        private TodoEditDialog(TodoItem source) {
            setTitle("Aufgabe bearbeiten");
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            titleInput.setText(source.getTitle());
            descriptionInput.setText(source.getDescription());
            descriptionInput.setWrapText(true);
            datePicker.setValue(source.getDeadline());

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Titel"), 0, 0);
            grid.add(titleInput, 1, 0);
            grid.add(new Label("Beschreibung"), 0, 1);
            grid.add(descriptionInput, 1, 1);
            grid.add(new Label("Fällig am"), 0, 2);
            grid.add(datePicker, 1, 2);

            getDialogPane().setContent(grid);
            setResultConverter(buttonType -> buttonType == ButtonType.OK ? buildResult(source) : null);
        }

        private TodoItem buildResult(TodoItem source) {
            source.setTitle(titleInput.getText().trim());
            source.setDescription(descriptionInput.getText().trim());
            source.setDeadline(Optional.ofNullable(datePicker.getValue()).orElse(LocalDate.now()));
            return source;
        }
    }
}
