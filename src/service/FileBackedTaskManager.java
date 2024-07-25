package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(Subtask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(Subtask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    private void save() {
        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(file.toString()))) {
            bwr.write(CSVFormatter.getHEADER());
            bwr.newLine();

            for (Epic epic : getListAllEpics()) {
                bwr.write(CSVFormatter.toStringSCV(epic, TaskType.EPIC));
                bwr.newLine();
            }
            for (Subtask subtask : getListAllSubTasks()) {
                bwr.write(CSVFormatter.toStringSCV(subtask, TaskType.SUBTASK));
                bwr.newLine();
            }
            for (Task task : getListAllTasks()) {
                bwr.write(CSVFormatter.toStringSCV(task, TaskType.TASK));
                bwr.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error writing to the file", e);

        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(path);
        try {
            List<String> tasksFields = Files.readAllLines(path);
            if (tasksFields.isEmpty()) {
                return taskManager;
            }
            int maxId = 0;
            for (int i = 1; i < tasksFields.size(); i++) {
                String line = tasksFields.get(i);
                Task task = CSVFormatter.fromStringCSV(line);
                int id = task.getId();
                if (id > maxId) {
                    maxId = id;
                }
                if (task instanceof Epic) {
                    taskManager.epics.put(id, (Epic) task);
                } else if (task instanceof Subtask) {
                    taskManager.subTasks.put(id, (Subtask) task);
                    Epic epic = taskManager.epics.get(task.getEpicId());
                    epic.getSubTaskIdList().add(id);
                } else {
                    taskManager.tasks.put(id, task);
                }
            }
            taskManager.setCurrentId(maxId + 1);
            return taskManager;

        } catch (IOException e) {
            throw new ManagerSaveException("Error when reading from a file", e);
        }
    }

}