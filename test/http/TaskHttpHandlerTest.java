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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskHttpHandlerTest extends HttpHandlersTest {

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task1");
        String taskJson = gson.toJson(task1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListAllTasks();

        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertTrue(tasksFromManager.stream().anyMatch(task -> task.getName().equals("Task1")), "Задача не добавилась");

    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task(task);
        task1.setDescription("testUpdateTask");
        String taskJson = gson.toJson(task1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListAllTasks();

        assertEquals(1, tasksFromManager.size(), "Задача добавилась вместо того чтобы обновиться");
        assertEquals("testUpdateTask", tasksFromManager.getFirst().getDescription(), "Задача не обновилась");


    }

    @Test
    void testAddIntersectionTask() throws IOException, InterruptedException {
        Task task1 = new Task(task);
        task1.setId(0);
        String taskJson = gson.toJson(task1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListAllTasks();

        assertEquals(1, tasksFromManager.size(), "Удалось добавить задачу с пересечением во времени");
    }

    @Test
    void testUseNotAllowedMethod() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/");
            HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString("0000")).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(405, response.statusCode());
    }

    @Test
    void testGetNonExistTask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + (task.getId() + 1));
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getListAllTasks();

        assertTrue(tasksFromManager.isEmpty(), "Задача не удалилась");
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task1");
        taskManager.addTask(task1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Task> tasks = gson.fromJson(response.body(), new TypeTokenListTask().getType());
        List<Task> tasksFromManager = taskManager.getListAllTasks();

        assertEquals(tasksFromManager, tasks, "Метод получения всех задач отработал некорретно");
    }

    @Test
    void testGetTaskByID() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        Task responseTask = gson.fromJson(response.body(), Task.class);

        assertEquals(task, responseTask, "Задача по Id возвращается некорретно");
    }

}


