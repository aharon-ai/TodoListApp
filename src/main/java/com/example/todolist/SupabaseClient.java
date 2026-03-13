package com.example.todolist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;






public class SupabaseClient {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String BASE_URL = Optional.ofNullable(System.getenv("SUPABASE_URL")).orElse("").trim();
    private static final String API_KEY = Optional.ofNullable(System.getenv("SUPABASE_KEY")).orElse("").trim();
    private static final String TABLE = "todos";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    public static List<TodoItem> fetchTodos() throws IOException {
        requireConfig();
        String url = String.format("%s?select=*&order=id.asc", resourceUrl());
        //System.out.println("Debug URL: " + url); // TODO: Zumtesten und loschen
        HttpRequest request = baseBuilder(url).GET().build();
        HttpResponse<String> response = send(request);
        if (response.statusCode() != 200) {
            throw new IOException("Fehler beim Laden: " + response.body());
        }
        TodoItem[] items = GSON.fromJson(response.body(), TodoItem[].class);
        if (items == null || items.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(items));
    }

    /**
    public static List<TodoItem> fetchTodos() throws IOException {
        requireConfig();
        String url = String.format("%s?select=*&order=id.asc", resourceUrl());

        // DEBUG: Zeigt uns genau, was passiert
        System.out.println("Anfrage an: " + url);

        HttpRequest request = baseBuilder(url).GET().build();
        HttpResponse<String> response = send(request);

        // DEBUG: Zeigt den Status-Code (z.B. 401, 404, 403)
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Antwort: " + response.body());

        if (response.statusCode() != 200) {
            throw new IOException("Fehler " + response.statusCode() + ": " + response.body());
        }

        TodoItem[] items = GSON.fromJson(response.body(), TodoItem[].class);
        return items == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(items));
    } **/

    public static TodoItem createTodo(TodoItem todo) throws IOException {
        requireConfig();
        String url = resourceUrl();
        HttpRequest request = baseBuilder(url)
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(payloadJson(todo)))
                .build();
        HttpResponse<String> response = send(request);
        if (response.statusCode() != 201) {
            throw new IOException("Fehler beim Erstellen: " + response.body());
        }
        TodoItem[] created = GSON.fromJson(response.body(), TodoItem[].class);
        if (created == null || created.length == 0) {
            return todo;
        }
        return created[0];
    }

    public static TodoItem updateTodo(TodoItem todo) throws IOException {
        requireConfig();
        if (todo.getId() == null) {
            throw new IllegalArgumentException("Todo muss eine ID haben.");
        }
        String url = String.format("%s?id=eq.%d", resourceUrl(), todo.getId());
        HttpRequest request = baseBuilder(url)
                .header("Prefer", "return=representation")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(payloadJson(todo)))
                .build();
        HttpResponse<String> response = send(request);
        if (response.statusCode() != 200) {
            throw new IOException("Fehler beim Aktualisieren: " + response.body());
        }
        TodoItem[] updated = GSON.fromJson(response.body(), TodoItem[].class);
        if (updated == null || updated.length == 0) {
            return todo;
        }
        return updated[0];
    }

    public static void deleteTodo(TodoItem todo) throws IOException {
        requireConfig();
        if (todo.getId() == null) {
            throw new IllegalArgumentException("Todo muss eine ID haben.");
        }
        String url = String.format("%s?id=eq.%d", resourceUrl(), todo.getId());
        HttpRequest request = baseBuilder(url).DELETE().build();
        HttpResponse<String> response = send(request);
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw new IOException("Fehler beim Löschen: " + response.body());
        }
    }

    private static HttpRequest.Builder baseBuilder(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
    }

    private static HttpResponse<String> send(HttpRequest request) throws IOException {
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Anfrage unterbrochen", e);
        }
    }

    private static String payloadJson(TodoItem todo) {
        JsonObject payload = new JsonObject();
        payload.addProperty("title", Optional.ofNullable(todo.getTitle()).orElse(""));
        payload.addProperty("description", Optional.ofNullable(todo.getDescription()).orElse(""));
        LocalDate deadline = Optional.ofNullable(todo.getDeadline()).orElse(LocalDate.now());
        payload.addProperty("deadline", deadline.toString());
        payload.addProperty("done", todo.isDone());
        return GSON.toJson(payload);
    }

    private static void requireConfig() {
        if (!isConfigured()) {
            throw new IllegalStateException("SUPABASE_URL und SUPABASE_KEY müssen als Umgebungsvariablen gesetzt sein.");
        }
    }

    private static String resourceUrl() {
        return BASE_URL.replaceAll("/+$", "") + "/" + TABLE;
    }

    public static boolean isConfigured() {
        return !BASE_URL.isEmpty() && !API_KEY.isEmpty();
    }
}
