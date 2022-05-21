package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.managerscollection.interfaces.HistoryManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class Managers {

    public static TaskManager getDefault() throws URISyntaxException {
        return new HTTPTaskManager(new URI("http://localhost:8078/"));
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        return FileBackedTasksManager.loadFromFile(new File("savedData.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}