package ru.yandex.practicum.managerscollection.interfaces;

import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(long id);

    List<Task> getHistory();

    int getSize();
}

