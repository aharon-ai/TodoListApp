package com.example.todolist;

import javafx.collections.ObservableList;

import java.io.*;
import java.time.LocalDate;

public class FileHandler {

    private static final String FILE_NAME = "todolist.json";

    public static void saveTasks(ObservableList<TodoItem> items) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (TodoItem item : items) {
                // Speicher: Title, Beschreibung, Data, Status
                writer.write(String.format("%s;%s;%s;%b%n",
                        item.getTitle(), item.getDescription(), item.getDeadline(),item.isDone())                        );
            }
        } catch (Exception e) {
            System.out.printf("Fahler beim Speichern: " + e.getMessage());
        }
    }

    // Lädt die Aufgaben aus der Datei in die Liste
    public static void loadTask(ObservableList<TodoItem> items) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;  // Wenn keine Datei da ist, brich einfach ab.

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    String title = parts[0];
                    String description = parts[1];
                    LocalDate deadline = LocalDate.parse(parts[2]);
                    Boolean done = Boolean.parseBoolean(parts[3]);

                    TodoItem item = new TodoItem(parts[0], parts[1], LocalDate.parse(parts[2]));
                    item.setDone(item.isDone());
                    items.add(item);
                }
            }
        } catch (Exception e) {
            System.out.printf("Fahler beim Laden: " + e.getMessage());
        }
    }


}
