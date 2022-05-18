package ru.yandex.practicum.managerscollection.main;

import ru.yandex.practicum.managerscollection.FileBackedTasksManager;
import ru.yandex.practicum.managerscollection.Managers;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.File;

import static ru.yandex.practicum.managerscollection.interfaces.TaskStatus.NEW;

public class Main {
    public static void main(String[] args) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new File("savedData.csv"));
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача №1", "Описание задачи №1 ", NEW, manager.addId());
        manager.addTask(task1);
        fileBackedTasksManager.addTask(task1);

        Task task2 = new Task("Задача №2", "Описание задачи №2 ", NEW, manager.addId());
        manager.addTask(task2);
        fileBackedTasksManager.addTask(task2);

        // Создаем 2 эпика, первый в 3-мя подзадачами
        Epic epic1 = new Epic("Убраться в квартире", "Пропылесосить", NEW, manager.addId());
        manager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Сделать уборку на балконе", "Протереть окна", NEW, manager.addId(), (epic1.getTaskId()));
        manager.addSubTask(subtask1);
        fileBackedTasksManager.addSubTask(subtask1);

        Subtask subtask2 = new Subtask("Сделать уборку в гардеробной", "Разобрать вещи", NEW, manager.addId(), (epic1.getTaskId()));
        manager.addSubTask(subtask2);
        fileBackedTasksManager.addSubTask(subtask2);

        Subtask subtask3 = new Subtask("Сделать уборку на убраться в спальне", "Пропылесосить", NEW, manager.addId(), (epic1.getTaskId()));
        manager.addSubTask(subtask3);
        fileBackedTasksManager.addSubTask(subtask3);


        // Второй без подзадач
        Epic epic2 = new Epic("Помыть машину", "Очень грязная", NEW, manager.addId());
        manager.addEpic(epic2);
        fileBackedTasksManager.addEpic(epic2);
        System.out.println("\n");
        // Последовательная история вызова
        manager.getTaskById(1L);
        manager.getTaskById(2L);
        manager.getEpicById(3L);
        manager.getSubTaskById(4L);
        manager.getSubTaskById(5L);
        manager.getSubTaskById(6L);
        manager.getEpicById(7L);
        System.out.println("Последовательная история вызова: " + manager.history());
        System.out.println(" ");
        // Вызов в разном порядке
        manager.getTaskById(2L);
        manager.getSubTaskById(4L);
        manager.getSubTaskById(5L);
        manager.getSubTaskById(6L);
        manager.getEpicById(7L);
        manager.getTaskById(1L);
        manager.getEpicById(3L);
        System.out.println("Вызов в разном порядке: " + manager.history());
        System.out.println(" ");
        manager.getTaskById(1L);
        manager.getTaskById(2L);
        manager.getEpicById(3L);
        manager.getSubTaskById(4L);
        manager.getSubTaskById(5L);
        manager.getSubTaskById(6L);
        manager.getEpicById(7L);
        // удаление задачи 2
        System.out.println("Удаление задачи 2" + manager.history());
        System.out.println(" ");
        System.out.println("Удаление эпика + 3 сабтаска" + manager.history());
    }
}