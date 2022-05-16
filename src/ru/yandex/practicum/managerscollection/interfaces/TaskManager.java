package ru.yandex.practicum.managerscollection.interfaces;

import ru.yandex.practicum.exception.ManagerSaveException;
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

    void addTask(Task task) throws ManagerSaveException;

    void addEpic(Epic epic) throws ManagerSaveException;

    void addSubTask(Subtask subtask) throws ManagerSaveException;

    Task getTaskById(Long id) throws ManagerSaveException;

    Epic getEpicById(Long id) throws ManagerSaveException;

    Subtask getSubTaskById(Long id) throws ManagerSaveException;

    void deleteTask(Long id) throws ManagerSaveException;

    void deleteEpic(Long id) throws ManagerSaveException;

    void deleteSubTask(Long id) throws ManagerSaveException;

    void updateTask(Task task) throws ManagerSaveException;

    void updateEpic(Epic epic);

    void updateSubTask(Subtask subtask) throws ManagerSaveException;

    HashMap<Long, Task> getTaskMap();
    HashMap<Long, Epic> getEpicMap();
    HashMap<Long, Subtask> getSubTaskMap();

    ArrayList<Task> getListTask();

    ArrayList<Epic> getListEpic();

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getListSubTaskFromEpic(Long idEpicTask);
}
