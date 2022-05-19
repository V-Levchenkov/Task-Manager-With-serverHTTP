package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.managerscollection.interfaces.TaskManager;

import java.io.File;

public class HTTPTaskManager extends FileBackedTasksManager implements TaskManager  {

    public HTTPTaskManager(File fileName) {
        super(fileName);
    }
}
