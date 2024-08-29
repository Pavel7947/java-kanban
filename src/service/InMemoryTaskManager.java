package service;

import exceptions.IntersectException;
import exceptions.NotFoundException;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subTasks;
    protected final Map<Integer, Epic> epics;
    protected int currentId;
    private final HistoryManager historyManager;
    protected final Set<Task> setByPriority;

    public InMemoryTaskManager() {
        currentId = 1;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Manager.getDefaultHistory();
        setByPriority = new TreeSet<>((task1, task2) -> {
            LocalDateTime stTask1 = task1.getStartTime();
            LocalDateTime stTask2 = task2.getStartTime();

            if (stTask1 == null && stTask2 == null) {
                return task1.getId() - task2.getId();
            } else if (stTask1 == null) {
                return 1;
            } else if (stTask2 == null) {
                return -1;
            } else {
                return stTask1.compareTo(stTask2);
            }
        });
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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            setByPriority.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Subtask subtask : subTasks.values()) {
            int idSubtask = subtask.getId();
            historyManager.remove(idSubtask);
            setByPriority.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            List<Integer> subtaskIdList = epic.getSubTaskIdList();
            if (subtaskIdList.isEmpty()) {
                continue;
            }
            subtaskIdList.clear();
            updateStatusEpic(epic);
            updateTimeEpic(epic);
        }
        subTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Subtask subtask : subTasks.values()) {
            historyManager.remove(subtask.getId());
            historyManager.remove(subtask.getEpicId());
            setByPriority.remove(subtask);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new NotFoundException("Задача с id: " + id + " не найдена");
        }
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            throw new NotFoundException("Подзадача с id: " + id + " не найдена");
        }
        Subtask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            throw new NotFoundException("Эпик с id: " + id + " не найден");
        }
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void addTask(Task task) {
        if (isIntersection(task)) {
            throw new IntersectException("Добавляемая задача пересекается во времени с другими " + task);
        }
        task.setId(currentId);
        tasks.put(currentId, task);
        setByPriority.add(task);
        currentId++;
    }

    @Override
    public void addSubTask(Subtask subTask) {
        int id = subTask.getEpicId();
        if (!epics.containsKey(id)) {
            throw new NotFoundException("Эпик с id: " + id + " в который добавляется подзадача не найден");
        }
        if (isIntersection(subTask)) {
            throw new IntersectException("Добавляемая подзадача пересекается во времени с другими " + subTask);
        }
        subTask.setId(currentId);
        subTasks.put(currentId, subTask);
        setByPriority.add(subTask);
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
            throw new NotFoundException("Задача с id: " + task.getId() + " не найдена");
        }
        if (isIntersection(task)) {
            throw new IntersectException("Обновляемая задача пересекается во времени с другими " + task);
        }
        setByPriority.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        setByPriority.add(task);
    }

    @Override
    public void updateSubTask(Subtask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            throw new NotFoundException("Подзадача с id: " + subTask.getId() + " не найдена");
        }
        if (isIntersection(subTask)) {
            throw new IntersectException("Обновляемая подзадача пересекается во времени с другими " + subTask);
        }
        setByPriority.remove(subTasks.get(subTask.getId()));
        subTasks.put(subTask.getId(), subTask);
        setByPriority.add(subTask);
        Epic epic = epics.get(subTask.getEpicId());
        updateStatusEpic(epic);
        updateTimeEpic(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new NotFoundException("Эпик с id: " + epic.getId() + " не найден");
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
        List<Subtask> subtaskList = epic.getSubTaskIdList().stream().map(subTasks::get).toList();

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = null;

        for (Subtask subtask : subtaskList) {
            if (subtask.getEndTime() == null) {
                continue;
            }
            if (startTime == null) {
                duration = subtask.getDuration();
                startTime = subtask.getStartTime();
                endTime = subtask.getEndTime();
                continue;
            }
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(setByPriority);
    }

    private Boolean isIntersection(Task task) {
        if (task.getEndTime() == null) {
            return false;
        }
        for (Task prTask : getPrioritizedTasks()) {
            LocalDateTime endTime = prTask.getEndTime();
            LocalDateTime startTime = prTask.getStartTime();
            LocalDateTime newEndTime = task.getEndTime();
            LocalDateTime newStartTime = task.getStartTime();

            if (task.getId() == prTask.getId()) {
                continue;
            }
            if (endTime == null) {
                break;
            }
            if (newEndTime.isAfter(startTime) && newStartTime.isBefore(endTime)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeTaskById(int id) {
        final Task task = tasks.remove(id);
        setByPriority.remove(task);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        final Subtask subTask = subTasks.remove(id);
        setByPriority.remove(subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTaskIdList().remove((Integer) id);
        updateStatusEpic(epic);
        updateTimeEpic(epic);
        historyManager.remove(id);

    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int idSubTask : epic.getSubTaskIdList()) {
            final Subtask subtask = subTasks.remove(idSubTask);
            setByPriority.remove(subtask);
            historyManager.remove(idSubTask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubTasksInEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("По указанному Id: " + id + " эпик не найден");
        }
        return epic.getSubTaskIdList().stream().map(subTasks::get).toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}



