package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import http.exceptions.NotAllowedRequestException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {

    public HistoryHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                List<Task> history = taskManager.getHistory();
                String response = gson.toJson(history);
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
        } finally {
            exchange.close();
        }
    }
}





