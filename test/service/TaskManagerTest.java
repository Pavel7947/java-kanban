package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected void fillingTaskManager() {
        task = new Task("Test addNewTask", "Test addNewTask description",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(450));
        taskManager.addTask(task);
        epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.addEpic(epic);
        subtask = new Subtask("Test addNewSubTask", "Test addNewSubTask description",
                Status.NEW, epic.getId(), LocalDateTime.now().minusDays(1), Duration.ofMinutes(450));
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

        assertThrows(NotFoundException.class, () -> taskManager.getSubTaskById(epic.getId())
                , "Эпик удалось добавить в самого себя в качестве подзадачи");
    }

    @Test
    void ASubtaskCanBeAnEpicOfItself() {
        Subtask savedSubTask = taskManager.getSubTaskById(subtask.getId());
        Subtask updateSubTask = new Subtask(savedSubTask.getName(), savedSubTask.getDescription(),
                savedSubTask.getStatus(), savedSubTask.getId());
        assertThrows(NotFoundException.class, () -> taskManager.addSubTask(updateSubTask)
                , "В качестве эпика удалось указать субтаску");
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

        assertThrows(NotFoundException.class, () -> taskManager.getEpicById(epic.getId())
                , "Эпик не удалился по ID");
        assertThrows(NotFoundException.class, () -> taskManager.getSubTaskById(subtask.getId())
                , "Субтаска после удаления эпика по ID не удалилась");
    }

    @Test
    void theEpicStatusShouldBeCalculatedCorrectly() {
        Subtask subtask1 = new Subtask("Test addNewSubTask1", "Test addNewSubTask1 description",
                Status.NEW, epic.getId(), LocalDateTime.now().minusDays(2), Duration.ofMinutes(450));
        Subtask subtask2 = new Subtask("Test addNewSubTask2", "Test addNewSubTask2 description",
                Status.NEW, epic.getId(), LocalDateTime.now().minusDays(3), Duration.ofMinutes(450));
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика расчитывается неправильно");

        Subtask updatedSubtask2 = new Subtask(subtask2);
        updatedSubtask2.setStatus(Status.DONE);
        taskManager.updateSubTask(updatedSubtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика расчитывается неправильно");

        Subtask updatedSubtask = new Subtask(subtask);
        Subtask updatedSubtask1 = new Subtask(subtask1);
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask1.setStatus(Status.DONE);
        taskManager.updateSubTask(updatedSubtask);
        taskManager.updateSubTask(updatedSubtask1);
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика расчитывается неправильно");

        subtask.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subtask);
        taskManager.updateSubTask(subtask1);
        taskManager.updateSubTask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика расчитывается неправильно");
    }

    @Test
    void theTimeIntersectionShouldBeCalculatedCorrectly() {
        Task updateTask = new Task(task);
        updateTask.setId(0);

        assertThrows(IntersectException.class, () -> taskManager.addTask(updateTask), "Удалось добавить 2 задачи с одинаковым временным интервалом");

        updateTask.setStartTime(updateTask.getStartTime().plusMinutes(200));
        assertThrows(IntersectException.class, () -> taskManager.addTask(updateTask), "Удалось добавить задачу с пересекающимся временным интервалом");

        updateTask.setStartTime(updateTask.getStartTime().plusMinutes(250));
        taskManager.addTask(updateTask);
        assertEquals(2, taskManager.getListAllTasks().size(), "Не удалось добавить задачу интервал времени которой не пересекается с другими задачами");
    }


}
