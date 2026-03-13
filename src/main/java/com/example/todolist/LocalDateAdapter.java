package com.example.todolist;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        return LocalDate.parse(json.getAsString());
    }
}
