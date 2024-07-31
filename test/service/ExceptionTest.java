package service;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {
    @Test
    void theIOExceptionMustBeConvertedManagerSaveException() {
        assertThrows(ManagerSaveException.class, () -> Manager.getFileBackedTaskManager(Path.of("000")));
    }

}
