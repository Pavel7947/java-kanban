package http;

import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHttpHandlerTest extends HttpHandlersTest {

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Epic1");
        String taskJson = gson.toJson(epic1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getListAllEpics();

        assertEquals(2, epicsFromManager.size(), "Некорректное количество задач");
        assertTrue(epicsFromManager.stream().anyMatch(epic -> epic.getName().equals("Epic1")), "Эпик не добавился");
    }

    @Test
    void testUseNotAllowedMethod() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString("0000")).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(405, response.statusCode());
    }

    @Test
    void testGetNonExistEpic() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + (epic.getId() + 1));
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManager = taskManager.getListAllEpics();

        assertTrue(epicsFromManager.isEmpty(), "Эпик не удалился");
    }

    @Test
    void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Epic1");
        taskManager.addEpic(epic1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Epic> epics = gson.fromJson(response.body(), new TypeTokenListEpic().getType());
        List<Epic> epicsFromManager = taskManager.getListAllEpics();

        assertEquals(epicsFromManager, epics, "Метод получения всех эпиков отработал некорретно");
    }

    @Test
    void testGetEpicByID() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic, responseEpic, "Эпик по Id возвращается некорретно");
    }

    @Test
    void testGetSubtasksInEpic() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeTokenListSubtask().getType());
        List<Subtask> subtasksFromManager = taskManager.getSubTasksInEpic(epic.getId());

        assertEquals(subtasksFromManager, subtasks, "Подзадачи эпика возвращаются некорретно");
    }
}