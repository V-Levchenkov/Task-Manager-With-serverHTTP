
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.ManagerSaveException;
import ru.yandex.practicum.managerscollection.FileBackedTasksManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;


import static org.junit.jupiter.api.Assertions.*;

import java.io.File;


public class FileBackedTasksManagerTest extends TaskManagerTest {
    public FileBackedTasksManagerTest() throws ManagerSaveException {
        super(new FileBackedTasksManager(new File("savedData.csv")));
    }

    private final File file = new File("TestSaveTasks.csv");
    private final FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
    private final FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
    private final Task task1 = new Task("Test Task1", "Test description1");
    private final Task task2 = new Task("Test Task2", "Test description2", TaskStatus.IN_PROGRESS);
    private final Task task3 = new Task("Test Task3", "Test description3", TaskStatus.DONE);
    private final Epic epic1 = new Epic("Test epicTask1", "Test description4");
    private final Epic epic2 = new Epic("Test epicTask2", "Test description5");
    private final Epic epic3 = new Epic("Test epicTask3", "Test description6");
    private final Subtask subTask1 = new Subtask("sub1", "desc", TaskStatus.NEW, epic1.getTaskId());
    private final Subtask subTask2 = new Subtask("sub2", "desc", TaskStatus.IN_PROGRESS, epic1.getTaskId());
    private final Subtask subTask3 = new Subtask("sub3", "desc", TaskStatus.DONE, epic1.getTaskId());
    private final Subtask subTask4 = new Subtask("sub4", "desc", TaskStatus.NEW, epic1.getTaskId());
    private final Subtask subTask5 = new Subtask("sub5", "desc", TaskStatus.DONE, epic2.getTaskId());
    private final Subtask subTask6 = new Subtask("sub6", "desc", TaskStatus.NEW, epic2.getTaskId());
    private final Subtask subTask7 = new Subtask("sub7", "desc", TaskStatus.DONE, epic3.getTaskId());


    @Test
    void emptyFileNameTest() {
        final Task task10 = new Task("Test Task1", "Test description1");
        FileBackedTasksManager fileBackedTasksManager1 = new FileBackedTasksManager(new File(""));
        ManagerSaveException ex = assertThrows(ManagerSaveException.class, () -> fileBackedTasksManager1.addTask(task10));
        assertEquals("Произошла ошибка во время записи в файл.", ex.getMessage());
    }

    @Test
    void addInFileTest() throws ManagerSaveException {
        assertTrue(fileBackedTasksManager.history().isEmpty(), "Список не пустой");
        assertTrue(fileBackedTasksManager.getAllTasks().isEmpty(), "Список не пустой");
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.addTask(task3);

        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);
        fileBackedTasksManager.addEpic(epic3);

        subTask1.setEpicId(epic1.getTaskId());
        subTask2.setEpicId(epic1.getTaskId());
        subTask3.setEpicId(epic1.getTaskId());
        subTask4.setEpicId(epic1.getTaskId());
        subTask5.setEpicId(epic2.getTaskId());
        subTask6.setEpicId(epic2.getTaskId());
        subTask7.setEpicId(epic3.getTaskId());

        fileBackedTasksManager.addSubTask(subTask1);
        fileBackedTasksManager.addSubTask(subTask2);
        fileBackedTasksManager.addSubTask(subTask3);
        fileBackedTasksManager.addSubTask(subTask4);
        fileBackedTasksManager.addSubTask(subTask5);
        fileBackedTasksManager.addSubTask(subTask6);
        fileBackedTasksManager.addSubTask(subTask7);

        fileBackedTasksManager.getTaskById(task1.getTaskId());
        fileBackedTasksManager.getTaskById(task2.getTaskId());
        fileBackedTasksManager.getTaskById(task3.getTaskId());

        fileBackedTasksManager.getEpicById(epic1.getTaskId());
        fileBackedTasksManager.getEpicById(epic2.getTaskId());
        fileBackedTasksManager.getEpicById(epic3.getTaskId());

        fileBackedTasksManager.getSubTaskById(subTask1.getTaskId());
        fileBackedTasksManager.getSubTaskById(subTask2.getTaskId());
        fileBackedTasksManager.getSubTaskById(subTask3.getTaskId());
        fileBackedTasksManager.getSubTaskById(subTask4.getTaskId());
        fileBackedTasksManager.getSubTaskById(subTask5.getTaskId());
        fileBackedTasksManager.getSubTaskById(subTask6.getTaskId());
        fileBackedTasksManager.getSubTaskById(subTask7.getTaskId());
        assertEquals(13, fileBackedTasksManager.getAllTasks().size(), "размер AllTasks() отличается"
                + " от запрошенных задач задач");
        assertEquals(13, fileBackedTasksManager.history().size(), "размер history() отличается"
                + " от запрошенных задач задач");
        System.out.println("Список задач после сохранения:");
        for (Task task : fileBackedTasksManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("история после сохранения:");
        for (Task task : fileBackedTasksManager.history()) {
            System.out.println(task);
        }
    }

    @Test
    void loadFromFileTest() throws ManagerSaveException {
        assertFalse(fileBackedTasksManager1.history().isEmpty(), "Список истории пустой после загрузки из файла");
        assertFalse(fileBackedTasksManager1.getAllTasks().isEmpty(), "Список задач пустой после загрузки из файла");
        fileBackedTasksManager1.addTask(task1);
        fileBackedTasksManager1.addTask(task2);
        fileBackedTasksManager1.addTask(task3);
        fileBackedTasksManager1.getTaskById(task1.getTaskId());
        fileBackedTasksManager1.getTaskById(task2.getTaskId());
        fileBackedTasksManager1.getTaskById(task3.getTaskId());
        System.out.println("Список задач после загрузки:");
        for (int i = 0; i < fileBackedTasksManager1.getAllTasks().size(); i++) {
            System.out.println((i + 1) + ": " + fileBackedTasksManager1.getAllTasks().get(i));
        }

        System.out.println("история после загрузки:");
        for (int i = 0; i < fileBackedTasksManager1.history().size(); i++) {
            System.out.println((i + 1) + ": " + fileBackedTasksManager1.history().get(i));
        }
    }
}