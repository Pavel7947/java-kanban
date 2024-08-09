package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import http.exceptions.IdFormatException;
import http.exceptions.NotAllowedRequestException;
import model.Subtask;
import service.IntersectException;
import service.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class SubtaskHttpHandler extends BaseHttpHandler {

    public SubtaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String idPath = path.replace("/subtasks/", "");
                        int id = parsePathId(idPath);
                        Subtask subtask = taskManager.getSubTaskById(id);
                        String response = gson.toJson(subtask);
                        sendText(exchange, response);
                    } else {
                        List<Subtask> subtaskList = taskManager.getListAllSubTasks();
                        String response = gson.toJson(subtaskList);
                        sendText(exchange, response);
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String idPath = path.replace("/subtasks/", "");
                        int id = parsePathId(idPath);
                        taskManager.removeSubTaskById(id);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        throw new NotAllowedRequestException("Недопустимый путь запроса для метода DELETE: " + path);
                    }
                    break;
                case "POST":
                    Subtask subtask = getSubtaskFromJson(exchange);
                    int id = subtask.getId();
                    if (id != 0) {
                        taskManager.updateSubTask(subtask);
                        exchange.sendResponseHeaders(201, 0);
                    } else {
                        taskManager.addSubTask(subtask);
                        exchange.sendResponseHeaders(201, 0);
                    }
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


    private Subtask getSubtaskFromJson(HttpExchange h) {
        String body = null;
        try (InputStream inputStream = h.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                throw new NotAllowedRequestException("Тело запроса оказалось пустым " + body);
            }
            return gson.fromJson(body, Subtask.class);
        } catch (Exception e) {
            throw new NotAllowedRequestException("Ошибка при преобразовании тела запроса: " + body);
        }
    }
}

