package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotAllowedRequestException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            try {
                String method = exchange.getRequestMethod();
                if (method.equals("GET")) {
                    List<Task> tasks = taskManager.getPrioritizedTasks();
                    String response = gson.toJson(tasks);
                    sendText(exchange, response);
                    return;
                }
                throw new NotAllowedRequestException("Метод: " + method + " не разрешен");
            } catch (NotAllowedRequestException e) {
                sendNotAllowed(exchange);
                System.out.println(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                sendServerError(exchange);
            }
        }
    }
}

