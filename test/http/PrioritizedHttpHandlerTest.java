package http;

import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHttpHandlerTest extends HttpHandlersTest {

    @Test
    void getPrioritizedList() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getPrioritizedTasks();
        String jsonList = gson.toJson(tasksFromManager);

        assertEquals(jsonList, response.body(), "Обработчик получения задач по приоритету работает неправильно");
    }

    @Test
    void testUseNotAllowedMethod() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString("0000")).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(405, response.statusCode());
    }
}


