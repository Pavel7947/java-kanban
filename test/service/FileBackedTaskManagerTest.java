package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private Path path;

    @Override
    @BeforeEach
    void createTaskManagerAndFillingOutTheTask() {
        try {
            path = File.createTempFile("Test", ".csv").toPath();
            taskManager = Manager.getFileBackedTaskManager(path);
            fillingTaskManager();
        } catch (IOException e) {
            throw new ManagerSaveException("Error creating a task manager", e);
        }
    }

    @Test
    void anEmptyFileShouldBeReadCorrectly() {
        try {
            Path newPath = File.createTempFile("Test", ".csv").toPath();
            FileBackedTaskManager newTaskManager = Manager.getFileBackedTaskManager(newPath);
            assertTrue(newTaskManager.getListAllTasks().isEmpty(), "Загрузка из пустого файла неправильная");
            assertTrue(newTaskManager.getListAllEpics().isEmpty(), "Загрузка из пустого файла неправильная");
            assertTrue(newTaskManager.getListAllSubTasks().isEmpty(), "Загрузка из пустого файла неправильная");
        } catch (IOException e) {
            throw new ManagerSaveException("Error creating a task manager", e);
        }
    }

    @Test
    void theTasksShouldBeSavedToTheFileCorrectly() {
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