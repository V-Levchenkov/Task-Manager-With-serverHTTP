package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.managerscollection.interfaces.HistoryManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
