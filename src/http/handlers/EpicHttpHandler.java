package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import http.exceptions.IdFormatException;
import http.exceptions.NotAllowedRequestException;
import model.Epic;
import model.Subtask;
import service.IntersectException;
import service.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String idPath = path.replace("/epics/", "").replace("/subtasks", "");
                        int id = parsePathId(idPath);
                        List<Subtask> subtasks = taskManager.getSubTasksInEpic(id);
                        String response = gson.toJson(subtasks);
                        sendText(exchange, response);
                    } else if (Pattern.matches("^/epics/\\d+$", path)) {
                        String idPath = path.replace("/epics/", "");
                        int id = parsePathId(idPath);
                        Epic epic = taskManager.getEpicById(id);
                        String responseEpic = gson.toJson(epic);
                        sendText(exchange, responseEpic);
                    } else {
                        List<Epic> epicList = taskManager.getListAllEpics();
                        String response = gson.toJson(epicList);
                        sendText(exchange, response);
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String idPath = path.replace("/epics/", "");
                        int id = parsePathId(idPath);
                        taskManager.removeEpicById(id);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        throw new NotAllowedRequestException("Недопустимый путь запроса для метода DELETE: " + path);
                    }
                    break;
                case "POST":
                    Epic epic = getEpicFromJson(exchange);
                    taskManager.addEpic(epic);
                    exchange.sendResponseHeaders(201, 0);
                    break;
                default:
                    throw new NotAllowedRequestException("Метод: " + method + " не разрешен");
            }
        } catch (NotAllowedRequestException | IdFormatException e) {
            System.out.println(e.getMessage());
            sendNotAllowed(exchange);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange);
        } catch (IntersectException e) {
            System.out.println(e.getMessage());
            sendHasInteractions(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendServerError(exchange);
        } finally {
            exchange.close();
        }
    }

    private Epic getEpicFromJson(HttpExchange h) {
        String body = null;
        try (InputStream inputStream = h.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                throw new NotAllowedRequestException("Тело запроса оказалось пустым " + body);
            }
            return gson.fromJson(body, Epic.class);
        } catch (Exception e) {
            throw new NotAllowedRequestException("Ошибка при преобразовании тела запроса: " + body);

        }
    }
}
