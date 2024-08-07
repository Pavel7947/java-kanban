package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getListAllTasks();

    List<Subtask> getListAllSubTasks();

    List<Epic> getListAllEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);

    void addTask(Task task);

    void addSubTask(Subtask subTask);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(Subtask subTask);

    List<Task> getPrioritizedTasks();

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubTaskById(int id);

    void removeEpicById(int id);

    List<Subtask> getSubTasksInEpic(int id);

    List<Task> getHistory();
}
