package ru.yandex.practicum.managerscollection.interfaces;

import ru.yandex.practicum.managerscollection.exception.ManagerSaveException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    ArrayList<Epic> getAllEpicsList();

    ArrayList<Task> getAllTasksList();

    ArrayList<SubTask> getAllSubTasksList();

    void clearTasks();

    void clearEpics();

    void clearSubTasks();

    Task getTaskById(long id);

    Task getSubTaskById(long id);

    Task getEpicById(long id);

    void addTask(Task task) throws ManagerSaveException;

    void addSubTask(SubTask subtask, long epicId) throws ManagerSaveException;

    void addEpic(Epic epic) throws ManagerSaveException;

    void updateEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void deleteEpicById(long id);

    void deleteTaskById(long id);

    void deleteSubTaskById(long id);

    List<Task> history();

    void removeFromHistoryByID(long id);

    Task getAllTasksById(long id);

    List<Task> getPrioritizedTask();

    HashMap<Long, Epic> getEpicsMap();

    public HashMap<Long, Task> getTasksMap();

    public HashMap<Long, SubTask> getSubTasksMap();

    ArrayList<SubTask> getEpicSubtasks(long id);
}

