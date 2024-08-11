package http.server;

import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import service.Manager;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

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
        TaskManager taskManager = Manager.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        System.out.println("Сервер запущен на порту 8080");
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

}
