package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import http.exceptions.IdFormatException;
import http.server.HttpTaskServer;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public abstract class BaseHttpHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager taskManager;


    public BaseHttpHandler(TaskManager taskManager) {
        this.gson = HttpTaskServer.getGson();
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
        System.out.println("Тело ответа успешно отправлено");
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        System.out.println("Клиенту вернулся 404 код статуса");
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        h.sendResponseHeaders(406, 0);
        System.out.println("Клиенту вернулся 406 код статуса");
    }

    protected void sendNotAllowed(HttpExchange h) throws IOException {
        h.sendResponseHeaders(405, 0);
        System.out.println("Клиенту вернулся 405 код статуса");
    }

    protected void sendServerError(HttpExchange h) {
        try {
            h.sendResponseHeaders(500, 0);
            System.out.println("Клиенту вернулся 500 код статуса");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected int parsePathId(String idPath) {
        try {
            return Integer.parseInt(idPath);
        } catch (NumberFormatException e) {
            System.out.println("Переданный id имеет неверный формат" + idPath);
            throw new IdFormatException("Переданный id имеет неверный формат" + idPath);
        }
    }

}
