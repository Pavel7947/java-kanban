package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.DateTimeAdapter;
import http.adapters.DurationAdapter;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class Manager {

    private Manager() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(Path path) {
        return FileBackedTaskManager.loadFromFile(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter()).create();
    }
}
