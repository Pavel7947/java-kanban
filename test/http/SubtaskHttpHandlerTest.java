package http;

import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskHttpHandlerTest extends HttpHandlersTest {

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1", Status.NEW, epic.getId());
        String subtaskJson = gson.toJson(subtask1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getListAllSubTasks();

        assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertTrue(subtasksFromManager.stream().anyMatch(subtask -> subtask.getName().equals("Subtask1")), "Подзадача не добавилась");

    }

    @Test
    void testUseNotAllowedMethod() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString("0000")).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(405, response.statusCode());
    }

    @Test
    void testGetNonExistSubtask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + (subtask.getId() + 1));
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    void testAddIntersectionSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask(subtask);
        subtask1.setId(0);
        String taskJson = gson.toJson(subtask1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListAllTasks();

        assertEquals(1, tasksFromManager.size(), "Удалось добавить подзадачу с пересечением во времени");
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask(subtask);
        subtask1.setDescription("testUpdateSubtask");
        String subtaskJson = gson.toJson(subtask1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getListAllSubTasks();

        assertEquals(1, subtasksFromManager.size(), "Подзадача добавилась вместо того чтобы обновиться");
        assertEquals("testUpdateSubtask", subtasksFromManager.getFirst().getDescription(), "Подзадача не обновилась");


    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = taskManager.getListAllSubTasks();

        assertTrue(subtasksFromManager.isEmpty(), "Подзадача не удалилась");
    }

    @Test
    void testGetAllSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1", Status.NEW, epic.getId());
        taskManager.addSubTask(subtask1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeTokenListSubtask().getType());
        List<Subtask> subtasksFromManager = taskManager.getListAllSubTasks();

        assertEquals(subtasksFromManager, subtasks, "Метод получения всех подзадач отработал некорретно");
    }

    @Test
    void testGetSubtaskByID() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(subtask, responseSubtask, "Подзадача по Id возвращается некорретно");
    }
}

