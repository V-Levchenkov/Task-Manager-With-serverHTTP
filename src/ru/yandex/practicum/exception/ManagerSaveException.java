package ru.yandex.practicum.exception;

import java.io.IOException;

public class ManagerSaveException extends IOException {

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerSaveException() {
    }
}
