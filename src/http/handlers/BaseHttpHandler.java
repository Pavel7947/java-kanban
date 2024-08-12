package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import exceptions.IdFormatException;
import service.Manager;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public abstract class BaseHttpHandler implements HttpHandler {
    protected static Gson gson = Manager.getGson();
    protected final TaskManager taskManager;


    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange httpExchange, String text) {
        try (httpExchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(200, resp.length);
            httpExchange.getResponseBody().write(resp);
            System.out.println("Тело ответа успешно отправлено");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке текта " + text, e);
        }
    }

    protected void sendNotFound(HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(404, 0);
            System.out.println("Клиенту вернулся 404 код статуса");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при возврате 404 кода статуса", e);
        }
    }

    protected void sendHasInteractions(HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(406, 0);
            System.out.println("Клиенту вернулся 406 код статуса");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при возврате 406 кода статуса", e);
        }
    }

    protected void sendNotAllowed(HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(405, 0);
            System.out.println("Клиенту вернулся 405 код статуса");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при возврате 405 кода статуса", e);
        }
    }

    protected void sendServerError(HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(500, 0);
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
