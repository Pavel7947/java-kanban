package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void addTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void tasksMustKeepThePreviousVersion() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId()); // Вызываем геттер для добаления в историю просмотров
        Task updateTask = new Task(task.getName(), task.getDescription(), Status.DONE);
        updateTask.setId(task.getId());
        taskManager.updateTask(updateTask);
        List<Task> historyTasks = taskManager.getHistory();
        Task historyTask = historyTasks.getFirst();

        assertFalse(historyTasks.isEmpty(), "Таска не сохранилась в историю просмотров");
        assertNotEquals(updateTask, historyTask, "Таска сохранилась неправильно");
    }
}