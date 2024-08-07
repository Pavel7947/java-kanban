import model.*;
import service.Manager;
import service.TaskManager;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Manager.getFileBackedTaskManager(Path.of("resources/Tasks.csv"));
        System.out.println("Поехали!");
        Task task1 = new Task("Таска 1", "Таска 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addTask(task1);
        Task task2 = new Task("Таска 2", "Таска 2", Status.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(30));
        taskManager.addTask(task2);
        taskManager.getPrioritizedTasks().forEach(System.out::println);
        System.out.println();
        Epic epic1 = new Epic("Эпик 1", "Эпик 1");
        taskManager.addEpic(epic1);

        Subtask subTask1 = new Subtask("Субтаска без времени", "Субтаска без времени", Status.NEW, epic1.getId());
        taskManager.addSubTask(subTask1);
        Subtask subTask2 = new Subtask("Субтаска 2", "Субтаска 2", Status.DONE, epic1.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30));
        taskManager.addSubTask(subTask2);
        Subtask subTask3 = new Subtask("Субтаска 3", "Субтаска 3", Status.NEW, epic1.getId(), LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(30));
        taskManager.addSubTask(subTask3);

        taskManager.getPrioritizedTasks().forEach(System.out::println);
    }

}