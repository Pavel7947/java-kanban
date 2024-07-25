import model.*;
import service.Manager;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();
        System.out.println("Поехали!");
        Task task1 = new Task("Сделать домашку", "Нужно успеть к пятнице", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Сдать финальный проект 6", "Нужно успеть к субботе", Status.NEW);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Закончить 1 модуль", "Главное все хорошо усвоить");
        taskManager.addEpic(epic1);

        Subtask subTask1 = new Subtask("Сдать финальный проект №4", "Нужно постараться", Status.NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        Subtask subTask2 = new Subtask("Сдать финальный проект №5", "Нужно постараться", Status.DONE, epic1.getId());
        taskManager.addSubTask(subTask2);
        Subtask subTask3 = new Subtask("Сдать финальный проект 3 спринта", "Нужно постараться", Status.NEW, epic1.getId());
        taskManager.addSubTask(subTask3);

        Epic epic2 = new Epic("Закончить 2 модуль", "Хорошо все усвоить");
        taskManager.addEpic(epic2);

        System.out.println("Печатаем все после добавления");
        printTasks(taskManager);


        Task updateTask1 = new Task(task1.getName(), task1.getDescription(), Status.IN_PROGRESS);
        updateTask1.setId(task1.getId());
        taskManager.updateTask(updateTask1);

        Subtask updateSubTask1 = new Subtask(subTask1.getName(), subTask1.getDescription(), Status.NEW, epic1.getId());
        updateSubTask1.setId(subTask1.getId());
        taskManager.updateSubTask(updateSubTask1);

        Epic updateEpic1 = new Epic(epic1.getName(), epic1.getDescription());
        updateEpic1.setId(epic1.getId());
        taskManager.updateEpic(updateEpic1);

        System.out.println();
        System.out.println("Печатаем все после обновления");
        printTasks(taskManager);
        System.out.println();
        System.out.println();

        System.out.println("Печатаем все после геттеров и удаления");
        taskManager.getEpicById(epic1.getId());
        System.out.println("Добавили в историю просмотров эпик №1");
        printBrowsingHistory(taskManager);
        taskManager.getSubTaskById(subTask1.getId());
        System.out.println("Добавили в историю просмотров одну подзадачу (Эпик  №1)");
        printBrowsingHistory(taskManager);
        taskManager.getTaskById(task1.getId());
        System.out.println("Добавили в историю просмотров одну задачу");
        printBrowsingHistory(taskManager);
        taskManager.removeEpicById(updateEpic1.getId());
        System.out.println("Удалили эпик");
        printBrowsingHistory(taskManager);
        taskManager.removeTaskById(updateTask1.getId());
        System.out.println("Удалили таску");
        printBrowsingHistory(taskManager);

    }

    public static void printTasks(TaskManager taskManager) {
        System.out.println("Печатаем эпики:");
        ArrayList<Epic> allEpics = (ArrayList<Epic>) taskManager.getListAllEpics();
        for (Epic allEpic : allEpics) {
            System.out.println(allEpic);
        }
        System.out.println("Печатаем таски:");
        ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getListAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }
        System.out.println("Печатаем субтаски:");
        ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getListAllSubTasks();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }
    }

    public static void printBrowsingHistory(TaskManager taskManager) {
        List<Task> history = taskManager.getHistory();
        if (history.isEmpty()) {
            System.out.println("История просмотров пустая");
            return;
        }
        System.out.println("Печатаем историю");
        for (int i = 0; i < history.size(); i++) {
            System.out.println("Таска номер " + (i + 1));
            System.out.println(history.get(i));
        }
    }

}
