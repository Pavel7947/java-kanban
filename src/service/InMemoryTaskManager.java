package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subTasks;
    protected final Map<Integer, Epic> epics;
    private int currentId;
    private final HistoryManager historyManager;

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Manager.getDefaultHistory();
    }

    @Override
    public List<Task> getListAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getListAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getListAllEpics() {
        return new ArrayList<>(epics.values());
    }


    @Override
    public void removeAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTaskIdList().clear();
            updateStatusEpic(epic);
        }

    }

    @Override
    public void removeAllEpics() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return null;
        }
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            return null;
        }
        Subtask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void addTask(Task task) {
        task.setId(currentId);
        tasks.put(currentId, task);
        currentId++;
    }

    @Override
    public void addSubTask(Subtask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        subTask.setId(currentId);
        subTasks.put(currentId, subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTaskIdList().add(currentId);
        updateStatusEpic(epic);
        currentId++;
    }


    @Override
    public void addEpic(Epic epic) {
        epic.setId(currentId);
        epics.put(currentId, epic);
        currentId++;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(Subtask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        updateStatusEpic(epics.get(subTask.getEpicId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        Epic updatedEpic = epics.get(epic.getId());
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());
    }

    private void updateStatusEpic(Epic epic) {
        if (epic.getSubTaskIdList().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        ArrayList<Subtask> subTasksList = getSubTasksInEpic(epic.getId());
        Status statusFirstElement = subTasksList.getFirst().getStatus();
        if (statusFirstElement == Status.IN_PROGRESS) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }
        for (Subtask subtask : subTasksList) {
            if (subtask.getStatus() != statusFirstElement) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        epic.setStatus(statusFirstElement);

    }


    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        Subtask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTaskIdList().remove((Integer) id);
        subTasks.remove(id);
        updateStatusEpic(epic);
        historyManager.remove(id);

    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int idSubTask : epic.getSubTaskIdList()) {
            subTasks.remove(idSubTask);
            historyManager.remove(idSubTask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Subtask> getSubTasksInEpic(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> subTaskArrayList = new ArrayList<>();
        for (int idSubTask : epic.getSubTaskIdList()) {
            subTaskArrayList.add(subTasks.get(idSubTask));
        }
        return subTaskArrayList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}



