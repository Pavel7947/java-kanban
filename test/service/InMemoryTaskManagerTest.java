package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    void createTaskManagerAndFillingOutTheTask() {
        taskManager = new InMemoryTaskManager();
        fillingTaskManager();
    }
}
