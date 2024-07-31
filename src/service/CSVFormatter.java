package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {
    private static final String HEADER = "id,type,name,status,description,epic,startTime,duration,endTime";

    private CSVFormatter() {
    }

    public static String toStringSCV(Task task, TaskType taskType) {
        return String.join(",", String.valueOf(task.getId()), taskType.toString(), task.getName(),
                String.valueOf(task.getStatus()), task.getDescription(), String.valueOf(task.getEpicId()),
                task.getStartTime().toString(), task.getDuration().toString(), task.getEndTime().toString());
    }

    public static Task fromStringCSV(String line) {
        String[] fields = line.split(",");
        TaskType taskType = TaskType.valueOf(fields[1]);

        switch (taskType) {
            case EPIC -> {
                Epic epic = new Epic(fields[2], fields[4], Status.valueOf(fields[3]),
                        LocalDateTime.parse(fields[6]), Duration.parse(fields[7]), LocalDateTime.parse(fields[8]));
                epic.setId(Integer.parseInt(fields[0]));
                return epic;
            }
            case SUBTASK -> {
                Subtask subtask = new Subtask(fields[2], fields[4], Status.valueOf(fields[3]),
                        Integer.parseInt(fields[5]), LocalDateTime.parse(fields[6]), Duration.parse(fields[7]));
                subtask.setId(Integer.parseInt(fields[0]));
                return subtask;
            }
            default -> {
                Task task = new Task(fields[2], fields[4], Status.valueOf(fields[3]),
                        LocalDateTime.parse(fields[6]), Duration.parse(fields[7]));
                task.setId(Integer.parseInt(fields[0]));
                return task;
            }
        }


    }

    public static String getHEADER() {
        return HEADER;
    }
}
