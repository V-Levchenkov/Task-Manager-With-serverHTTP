package ru.yandex.practicum.managerscollection.interfaces;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    List<Task> history();

    long addId();

    long getId();

    void deleteAll();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(Subtask subtask);

    Task getTaskById(Long id);

    Epic getEpicById(Long id);

    Subtask getSubTaskById(Long id);

    void deleteTask(Long id);

    void deleteEpic(Long id);

    void deleteSubTask(Long id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(Subtask subtask);

    HashMap<Long, Task> getTaskMap();

    HashMap<Long, Epic> getEpicMap();

    HashMap<Long, Subtask> getSubTaskMap();

    ArrayList<Task> getListTask();

    ArrayList<Epic> getListEpic();

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getListSubTaskFromEpic(Long idEpicTask);
}
