package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    void instancesMustBeInitialized() {
        TaskManager taskManager = Manager.getDefault();
        HistoryManager historyManager = Manager.getDefaultHistory();

        assertNotNull(taskManager, "Создание объекта TaskManager реализовано неправильно");
        assertNotNull(historyManager, "Создание объекта HistoryManager реализовано неправильно");
    }
}