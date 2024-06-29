import model.*;
import service.Manager;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();
        System.out.println("Поехали!");
        Task task = new Task("Сделать домашку", "Нужно успеть к пятнице", Status.NEW);
        taskManager.addTask(task);

        Epic epic1 = new Epic("Закончить 1 модуль", "Главное все хорошо усвоить");
        taskManager.addEpic(epic1);
        Subtask subTask1 = new Subtask("Сдать финальный проект №4", "Нужно постараться", Status.NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        Subtask subTask2 = new Subtask("Сдать финальный проект №5", "Нужно постараться", Status.DONE, epic1.getId());
        taskManager.addSubTask(subTask2);

        Epic epic2 = new Epic("Закончить 2 модуль", "Хорошо все усвоить");
        taskManager.addEpic(epic2);
        Subtask subTask3 = new Subtask("Сдать финальный проект 1 спринта", "Нужно постараться", Status.NEW, epic2.getId());
        taskManager.addSubTask(subTask3);

        System.out.println("Печатаем все после добавления");
        print(taskManager);


        Task updateTask = new Task(task.getName(), task.getDescription(), Status.IN_PROGRESS);
        updateTask.setId(task.getId());
        taskManager.updateTask(updateTask);

        Subtask updateSubTask1 = new Subtask(subTask1.getName(), subTask1.getDescription(), Status.NEW, epic1.getId());
        updateSubTask1.setId(subTask1.getId());
        taskManager.updateSubTask(updateSubTask1);

        Epic updateEpic1 = new Epic(epic1.getName(), epic1.getDescription());
        updateEpic1.setId(epic1.getId());
        taskManager.updateEpic(updateEpic1);

        System.out.println();
        System.out.println("Печатаем все после обновления");
        print(taskManager);
        System.out.println();
        System.out.println();

        System.out.println("Печатаем все после геттеров и удаления");
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task.getId());
        taskManager.removeEpicById(updateEpic1.getId());
        taskManager.removeTaskById(updateTask.getId());
        print(taskManager);

    }

    public static void print(TaskManager taskManager) {
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
        List<Task> history = taskManager.getHistory();
        if (history.isEmpty()) {
            System.out.println("История просмотров пустая");
            return;
        }
        System.out.println("Печатаем историю");
        for (int i = 0; i < history.size(); i++) {
            System.out.println("Таска номер " + (i+1));
            System.out.println(history.get(i));
        }

        }

}
