package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private Path path;

    @BeforeEach
    void addTaskManagerAndTasks() {
        try {
            path = File.createTempFile("Test", ".csv").toPath();
            taskManager = Manager.getFileBackedTaskManager(path);
            task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
            epic = new Epic("Test addNewEpic", "Test addNewEpic description");
            subtask = new Subtask("Test addNewSubTask", "Test addNewSubTask description", Status.NEW, epic.getId());
        } catch (IOException e) {
            throw new ManagerSaveException("Error creating a task manager", e);
        }
    }

    @Test
    void anEmptyFileShouldBeReadCorrectly() {
        assertTrue(taskManager.getListAllTasks().isEmpty(), "Загрузка из пустого файла неправильная");
        assertTrue(taskManager.getListAllEpics().isEmpty(), "Загрузка из пустого файла неправильная");
        assertTrue(taskManager.getListAllSubTasks().isEmpty(), "Загрузка из пустого файла неправильная");
    }

    @Test
    void theTasksShouldBeSavedToTheFileCorrectly() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);
        List<Task> savedTasks = taskManager.getListAllTasks();
        List<Epic> savedEpics = taskManager.getListAllEpics();
        List<Subtask> savedSubtask = taskManager.getListAllSubTasks();

        FileBackedTaskManager newTaskManager = Manager.getFileBackedTaskManager(path);
        List<Task> savedTasksNew = newTaskManager.getListAllTasks();
        List<Epic> savedEpicsNew = newTaskManager.getListAllEpics();
        List<Subtask> savedSubtaskNew = newTaskManager.getListAllSubTasks();

        assertEquals(savedTasks, savedTasksNew, "Таски восстановились из файла неправильно");
        assertEquals(savedEpics, savedEpicsNew, "Эпики восстановились из файла неправильно");
        assertEquals(savedSubtask, savedSubtaskNew, "Подзадачи восстановились из файла неправильно");
    }


}