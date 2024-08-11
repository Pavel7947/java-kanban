package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.IdFormatException;
import exceptions.NotAllowedRequestException;
import model.Task;
import exceptions.IntersectException;
import exceptions.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class TaskHttpHandler extends BaseHttpHandler {

    public TaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            try {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();

                switch (method) {
                    case "GET":
                        if (Pattern.matches("^/tasks/\\d+$", path)) {
                            String idPath = path.replace("/tasks/", "");
                            int id = parsePathId(idPath);
                            Task task = taskManager.getTaskById(id);
                            String responseTask = gson.toJson(task);
                            sendText(exchange, responseTask);
                        } else {
                            List<Task> taskList = taskManager.getListAllTasks();
                            String response = gson.toJson(taskList);
                            sendText(exchange, response);
                        }
                        break;
                    case "DELETE":
                        if (Pattern.matches("^/tasks/\\d+$", path)) {
                            String idPath = path.replace("/tasks/", "");
                            int id = parsePathId(idPath);
                            taskManager.removeTaskById(id);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            throw new NotAllowedRequestException("Недопустимый путь запроса для метода DELETE: " + path);
                        }
                        break;
                    case "POST":
                        Task task = getTaskFromJson(exchange);
                        int id = task.getId();
                        if (id != 0) {
                            taskManager.updateTask(task);
                            exchange.sendResponseHeaders(201, 0);
                        } else {
                            taskManager.addTask(task);
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
            }
        }
    }

    private Task getTaskFromJson(HttpExchange h) {
        String body = "";
        try (InputStream inputStream = h.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                throw new NotAllowedRequestException("Тело запроса оказалось пустым " + body);
            }
            return gson.fromJson(body, Task.class);
        } catch (Exception e) {
            throw new NotAllowedRequestException("Ошибка при преобразовании тела запроса: " + body);
        }
    }
}



