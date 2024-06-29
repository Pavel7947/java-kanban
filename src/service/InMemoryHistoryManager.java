package service;

import model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> browsingHistory;
    private static final int MAX_SIZE_LIST = 10;

    public InMemoryHistoryManager() {
        browsingHistory = new LinkedList<>();
    }

    @Override
    public  void add(Task task) {
        if (browsingHistory.size() == MAX_SIZE_LIST) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(task);
    }
    @Override
    public LinkedList<Task> getHistory() {
        if (browsingHistory.isEmpty()) {
            return new LinkedList<>();
        } else {
            return new LinkedList<>(browsingHistory);
        }
    }
}
