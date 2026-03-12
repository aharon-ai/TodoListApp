package com.example.todolist;

import java.time.LocalDate;

public class TodoItem {
    private Integer id;
    private String title;
    private String description;
    private LocalDate deadline;
    private boolean done;

    public TodoItem() {
        this("", "", LocalDate.now());
    }

    public TodoItem(String title, String description, LocalDate deadline) {
        this(null, title, description, deadline, false);
    }

    public TodoItem(Integer id, String title, String description, LocalDate deadline, boolean done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.done = done;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Fällig: %s)", id == null ? "neu" : id, title, deadline);
    }
}
