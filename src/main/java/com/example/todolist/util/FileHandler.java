package com.example.todolist.util;

import com.example.todolist.model.TodoItem;
import javafx.collections.ObservableList;

import java.io.*;
import java.time.LocalDate;

public class FileHandler {

    private static final String FILE_NAME = "todolist.txt";

    public static void saveTasks(ObservableList<TodoItem> items) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (TodoItem item : items) {
                // Speicher: Title, Beschreibung, Data, Status
                writer.write(String.format("%s;%s;%s;%b%n",
                        item.getTitle(), item.getDescription(), item.getDeadline(),item.isDone())                        );
            }
        }
    }

    public static void loadTask(ObservableList<TodoItem> items) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;  // Wenn keine Datei da ist, brich einfach ab.

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    TodoItem item = new TodoItem(parts[0], parts[1], LocalDate.parse(parts[2]));
                    item.setDone(Boolean.parseBoolean(parts[3]));
                    items.add(item);
                }
            }
        }
    }
}
