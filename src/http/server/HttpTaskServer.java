package http.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.adapters.DateTimeAdapter;
import http.adapters.DurationAdapter;
import http.handlers.*;
import service.Manager;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT),
                0);
        server.createContext("/tasks", new TaskHttpHandler(taskManager));
        server.createContext("/epics", new EpicHttpHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHttpHandler(taskManager));
        server.createContext("/history", new HistoryHttpHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager1 = Manager.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager1);
        taskServer.start();
        System.out.println("Сервер запущен на порту 8080");

    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter()).create();
    }
}
