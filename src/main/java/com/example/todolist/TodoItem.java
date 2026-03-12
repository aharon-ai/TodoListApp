package com.example.todolist;

import java.time.LocalDate;

public class TodoItem {
    private String title;
    private String description;
    private LocalDate deadline;
    private boolean isDone;
    private boolean done;

    public TodoItem(String title, String description, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isDone = false;
    }

    // Getter und Setter Initialisieren

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {

        return title + " (Fällig: " + deadline + ")";
    }
}
