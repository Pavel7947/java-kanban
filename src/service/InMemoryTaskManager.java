package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subTasks;
    protected final Map<Integer, Epic> epics;
    private int currentId;
    private final HistoryManager historyManager;
    protected final Set<Task> setByPriority;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Manager.getDefaultHistory();
        setByPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
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
        setByPriority.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        setByPriority.removeAll(subTasks.values());
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTaskIdList().clear();
            updateStatusEpic(epic);
            updateTimeEpic(epic);
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
        setByPriority.removeAll(subTasks.values());
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
        if (isIntersection(task)) {
            return;
        }
        task.setId(currentId);
        tasks.put(currentId, task);
        if (!task.getEndTime().equals(Task.DEFAULT_TIME)) {
            setByPriority.add(task);
        }
        currentId++;
    }

    @Override
    public void addSubTask(Subtask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        if (isIntersection(subTask)) {
            return;
        }
        subTask.setId(currentId);
        subTasks.put(currentId, subTask);
        if (!subTask.getEndTime().equals(Task.DEFAULT_TIME)) {
            setByPriority.add(subTask);
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTaskIdList().add(currentId);
        updateStatusEpic(epic);
        updateTimeEpic(epic);
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
        Task oldTask = tasks.get(task.getId());
        boolean isDeleted = setByPriority.remove(oldTask);
        if (isIntersection(task)) {
            if (isDeleted) {
                setByPriority.add(oldTask);
            }
            return;
        }
        tasks.put(task.getId(), task);
        if (!task.getEndTime().equals(Task.DEFAULT_TIME)) {
            setByPriority.add(task);
        }
    }

    @Override
    public void updateSubTask(Subtask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            return;
        }
        Subtask oldSubtask = subTasks.get(subTask.getId());
        boolean isDeleted = setByPriority.remove(oldSubtask);
        if (isIntersection(subTask)) {
            if (isDeleted) {
                setByPriority.add(oldSubtask);
            }
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        if (!subTask.getEndTime().equals(Task.DEFAULT_TIME)) {
            setByPriority.add(subTask);
        }
        Epic epic = epics.get(subTask.getEpicId());
        updateStatusEpic(epic);
        updateTimeEpic(epic);
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
        List<Subtask> subTasksList = getSubTasksInEpic(epic.getId());
        Status statusFirstElement = subTasksList.getFirst().getStatus();
        if (statusFirstElement == Status.IN_PROGRESS) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }
        boolean isProgress = subTasksList.stream().map(Subtask::getStatus).anyMatch(status -> status != statusFirstElement);
        if (isProgress) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }
        epic.setStatus(statusFirstElement);
    }

    private void updateTimeEpic(Epic epic) {
        List<Subtask> subtaskList = epic.getSubTaskIdList().stream().map(subTasks::get)
                .filter(subtask -> !subtask.getEndTime().equals(Task.DEFAULT_TIME)).toList();
        if (subtaskList.isEmpty()) {
            epic.setStartTime(Task.DEFAULT_TIME);
            epic.setEndTime(Task.DEFAULT_TIME);
            epic.setDuration(Task.DEFAULT_DURATION);
            return;
        }

        Optional<LocalDateTime> startTime = subtaskList.stream().map(Subtask::getStartTime).min(LocalDateTime::compareTo);
        Optional<LocalDateTime> endTime = subtaskList.stream().map(Subtask::getEndTime).max(LocalDateTime::compareTo);
        Duration duration = subtaskList.stream().map(Subtask::getDuration).reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(startTime.get());
        epic.setEndTime(endTime.get());
        epic.setDuration(duration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(setByPriority);
    }

    private Boolean isIntersection(Task task) {
        return getPrioritizedTasks().stream().anyMatch(task1 -> !(task.getStartTime().isAfter(task1.getEndTime())
                || task.getEndTime().isBefore(task1.getStartTime()) || task.getStartTime().equals(task1.getEndTime())
                || task.getEndTime().equals(task1.getStartTime())));
    }

    @Override
    public void removeTaskById(int id) {
        Task task = tasks.get(id);
        setByPriority.remove(task);
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        Subtask subTask = subTasks.get(id);
        setByPriority.remove(subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTaskIdList().remove((Integer) id);
        subTasks.remove(id);
        updateStatusEpic(epic);
        updateTimeEpic(epic);
        historyManager.remove(id);

    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int idSubTask : epic.getSubTaskIdList()) {
            Subtask subtask = subTasks.get(idSubTask);
            setByPriority.remove(subtask);
            subTasks.remove(idSubTask);
            historyManager.remove(idSubTask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubTasksInEpic(int id) {
        Epic epic = epics.get(id);
        return epic.getSubTaskIdList().stream().map(subTasks::get).toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}



