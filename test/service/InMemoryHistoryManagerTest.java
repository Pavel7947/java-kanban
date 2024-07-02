package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void addTaskManagerAndTasks() {
        taskManager = new InMemoryTaskManager();
        task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        task2 = new Task("Test addNewTask2", "Test addNewTask2 description", Status.NEW);
        task3 = new Task("Test addNewTask3", "Test addNewTask3 description", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

    }

    @Test
    void tasksMustKeepThePreviousVersion() {
        Task updateTask = new Task(task1.getName(), task1.getDescription(), Status.DONE);
        updateTask.setId(task1.getId());
        taskManager.updateTask(updateTask);
        List<Task> historyTasks = taskManager.getHistory();
        Task historyTask = historyTasks.getFirst();

        assertFalse(historyTasks.isEmpty(), "Таска не сохранилась в историю просмотров");
        assertNotEquals(updateTask, historyTask, "Таска сохранилась неправильно");
    }

    @Test
    void tasksMustBeSavedInTheCorrectOrder() {
        List<Task> historyTasks = taskManager.getHistory();
        List<Task> historyTasksCorrect = new ArrayList<>(List.of(task1, task2, task3));

        assertEquals(3, historyTasks.size(), "Сохранились не все задачи");
        assertEquals(historyTasksCorrect, historyTasks, "Задачи сохраняются в неправильном порядке");
    }

    @Test
    void tasksMustBeProperlyRemovedFromTheBeginning() {
        taskManager.removeTaskById(task1.getId());
        List<Task> historyTasks = taskManager.getHistory();
        assertEquals(task2, historyTasks.getFirst(), "Первая задача из истории удалилась неправильно");
    }

    @Test
    void tasksMustBeProperlyRemovedFromTheEnd() {
        taskManager.removeTaskById(task3.getId());
        List<Task> historyTasks = taskManager.getHistory();

        assertEquals(task2, historyTasks.getLast(), "Последняя задача из истории удалилась неправильно");
    }

    @Test
    void tasksMustBeProperlyRemovedFromTheMiddle() {
        taskManager.removeTaskById(task2.getId());
        List<Task> historyTasks = taskManager.getHistory();

        assertEquals(task3, historyTasks.get(1), "Задача из середины истории удалилась неправильно");
    }

    @Test
    void allTasksShouldBeDeleted() {
        taskManager.removeAllTasks();
        List<Task> historyTasks = taskManager.getHistory();

        assertEquals(0, historyTasks.size());
    }
}