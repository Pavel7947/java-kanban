package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.server.HttpTaskServer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.Manager;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpHandlersTest {
    protected TaskManager taskManager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    void createHttpTaskServer() throws IOException {
        taskManager = Manager.getDefault();
        fillingTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = Manager.getGson();
        taskServer.start();
    }

    @AfterEach
    void closeTaskServer() {
        taskServer.stop();
    }

    private void fillingTaskManager() {
        task = new Task("Test addNewTask", "Test addNewTask description",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(450));
        taskManager.addTask(task);
        epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.addEpic(epic);
        subtask = new Subtask("Test addNewSubTask", "Test addNewSubTask description",
                Status.NEW, epic.getId(), LocalDateTime.now().minusDays(1), Duration.ofMinutes(450));
        taskManager.addSubTask(subtask);
    }

    protected static class TypeTokenListEpic extends TypeToken<List<Epic>> {
    }

    protected static class TypeTokenListSubtask extends TypeToken<List<Subtask>> {
    }

    protected static class TypeTokenListTask extends TypeToken<List<Task>> {
    }
}
