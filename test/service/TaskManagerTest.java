package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected void fillingTaskManager() {
        task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.addEpic(epic);
        subtask = new Subtask("Test addNewSubTask", "Test addNewSubTask description", Status.NEW, epic.getId());
        taskManager.addSubTask(subtask);
    }

    @BeforeEach
    abstract void createTaskManagerAndFillingOutTheTask();

    @Test
    void addNewTask() {
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача по ID не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают. Ошибка в сравнении по ID");

        ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getListAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден по ID.");
        assertEquals(epic, savedEpic, "Эпики  не совпадают. Ошибка в сравнении по ID");

        List<Epic> epics = taskManager.getListAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void addNewSubTask() {
        Subtask savedSubTask = taskManager.getSubTaskById(subtask.getId());

        assertNotNull(savedSubTask, "Субтаска не найдена оп ID");
        assertEquals(subtask, savedSubTask, "Субтаски  не совпадают. Ошибка в сравнении по ID");

        List<Subtask> subtasks = taskManager.getListAllSubTasks();

        assertNotNull(subtasks, "Субтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество субтасок.");
        assertEquals(subtask, subtasks.getFirst(), "Субтаскт не совпадают.");
    }

    @Test
    void theSpecifiedAndGeneratedIdShouldNotConflict() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        Task updateTask = new Task(task.getName(), task.getDescription(), Status.DONE);
        updateTask.setId(task.getId());
        taskManager.updateTask(updateTask);
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotEquals(task, savedTask, "Задача не обновилась.");

    }

    @Test
    void AnEpicObjectCannotContainItSelf() {
        Subtask subtask2 = new Subtask(epic.getName(), epic.getDescription(), epic.getStatus(), epic.getId());
        subtask2.setId(epic.getId());
        taskManager.addSubTask(subtask2);
        Subtask saveSubTask = taskManager.getSubTaskById(epic.getId());

        assertNull(saveSubTask, "Ошибка. Эпик удалось добавить в самого себя в качестве подзадачи");
    }

    @Test
    void ASubtaskCanBeAnEpicOfItself() {
        Subtask savedSubTask = taskManager.getSubTaskById(subtask.getId());
        Subtask updateSubTask = new Subtask(savedSubTask.getName(), savedSubTask.getDescription(),
                savedSubTask.getStatus(), savedSubTask.getId());
        taskManager.addSubTask(updateSubTask);
        savedSubTask = taskManager.getSubTaskById(updateSubTask.getId());

        assertNull(savedSubTask, "В качестве эпика удалось указать субтаску");
    }

    @Test
    void AllEpicsShouldBeDeleted() {
        taskManager.removeAllEpics();
        List<Epic> allEpics = taskManager.getListAllEpics();
        List<Subtask> allSubTasks = taskManager.getListAllSubTasks();
        boolean isEmptyEpicsList = allEpics.isEmpty();
        boolean isEmptySubTasksList = allSubTasks.isEmpty();

        assertTrue(isEmptyEpicsList && isEmptySubTasksList, "Эпики удалились неправильно");

    }

    @Test
    void AllSubtasksShouldBeDeleted() {
        taskManager.removeAllSubTasks();
        List<Subtask> allSubTasks = taskManager.getListAllSubTasks();
        boolean isEmptySubTasksList = allSubTasks.isEmpty();

        assertTrue(isEmptySubTasksList, "Суб таски удалились неправильно");

    }

    @Test
    void SubtaskIDMustBeDeletedCorrectlyFromTheEpic() {
        taskManager.removeSubTaskById(subtask.getId());
        List<Integer> subTaskIdList = epic.getSubTaskIdList();

        assertEquals(0, subTaskIdList.size(), "ID неактуальных Субтасок хранится внутри Эпика");
    }

    @Test
    void theTaskMustBeCorrectlyDeletedByID() {
        taskManager.removeTaskById(task.getId());
        List<Task> tasks = taskManager.getListAllTasks();

        assertTrue(tasks.isEmpty(), "Таска не удалилась по ID");
    }

    @Test
    void theEpicMustBeCorrectlyDeletedByID() {
        taskManager.removeEpicById(epic.getId());
        Epic deletedEpic = taskManager.getEpicById(epic.getId());
        Subtask deletedSubtask = taskManager.getSubTaskById(subtask.getId());

        assertNull(deletedEpic, "Эпик не удалился по ID");
        assertNull(deletedSubtask, "Субтаска после удаления эпика по ID не удалилась");
    }
}
