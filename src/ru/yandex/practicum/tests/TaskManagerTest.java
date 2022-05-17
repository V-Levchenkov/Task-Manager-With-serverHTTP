
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.ManagerSaveException;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    public TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }
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
    void addTaskTest() {
        taskManager.addTask(task1);
        assertEquals(task1, taskManager.getTaskMap().get(task1.getTaskId()));
        assertNotNull(taskManager.getTaskMap(), "Задачи на возвращаются.");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(epic1));
        assertEquals("задача не соответствует типу Task", ex.getMessage());

        NullPointerException ex1 = assertThrows(NullPointerException.class, () -> taskManager.addTask(null));
        assertEquals("переданный task = null", ex1.getMessage());

    }

    @Test
    void addEpicTest() {
        taskManager.addEpic(epic1);
        assertEquals(epic1, taskManager.getEpicMap().get(epic1.getTaskId()));

        NullPointerException ex1 = assertThrows(NullPointerException.class, () -> taskManager.addEpic(null));
        assertEquals("переданный epicTask = null", ex1.getMessage());
        assertNotNull(taskManager.getEpicMap(), "Задачи на возвращаются.");
    }

    @Test
    void addSubTaskTest() {
        taskManager.addEpic(epic1);
        subTask1.setEpicId(epic1.getTaskId());
        taskManager.addSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubTaskMap().get(subTask1.getTaskId()));

        assertNotNull(taskManager.getSubTaskMap(), "Задачи нe возвращаются.");

        subTask2.setEpicId(123L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskManager.addSubTask(subTask2));
        assertEquals("id EpicTask записанный в subTask отсутствует в хранилище", ex.getMessage());

        NullPointerException ex1 = assertThrows(NullPointerException.class, () -> taskManager.addSubTask(null));
        assertEquals("переданный subTask = null", ex1.getMessage());
    }

    @Test
    public void GetTaskTest() {
        taskManager.addTask(task1);
        NullPointerException ex1 = assertThrows(NullPointerException.class, () -> taskManager.getTaskById(null));
        assertEquals("запрошенный null = 0", ex1.getMessage());

        Task newTask = taskManager.getTaskById(task1.getTaskId());
        assertNotNull(newTask, "Задача не найдена.");
        assertEquals(task1, newTask, "Задачи не совпадают.");

        Long getId1 = (long) 10;
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> taskManager.getTaskById(getId1));
        assertEquals("задача с idTask:" + getId1 + " отсутствует", ex2.getMessage());

        Long getId = (long) -1;
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> taskManager.getTaskById(getId));
        assertEquals("idTask с отрицательным значением", ex3.getMessage());
    }

    @Test
    public void GetEpicTest() {
        taskManager.addEpic(epic1);
        NullPointerException ex1 = assertThrows(NullPointerException.class, () -> taskManager.getEpicById(null));
        assertEquals("запрошенный idEpicTask = null", ex1.getMessage());

        Epic newTask = taskManager.getEpicById(epic1.getTaskId());
        assertNotNull(newTask, "Задача не найдена.");
        assertEquals(epic1, newTask, "Задачи не совпадают.");

        Long getId1 = (long) 10;
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> taskManager.getEpicById(getId1));
        assertEquals("задача с idEpicTask:" + getId1 + " отсутствует", ex2.getMessage());

        Long getId = (long) -1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskManager.getEpicById(getId));
        assertEquals("idEpicTask с отрицательным значением", ex.getMessage());
    }

    @Test
    public void getSubTaskTest() {
        taskManager.addEpic(epic1);
        subTask1.setEpicId(epic1.getTaskId());
        taskManager.addSubTask(subTask1);
        NullPointerException ex1 = assertThrows(NullPointerException.class, () -> taskManager.getSubTaskById(null));
        assertEquals("запрошенный idSubTask = null", ex1.getMessage());

        Subtask newTask = taskManager.getSubTaskById(subTask1.getTaskId());
        assertNotNull(newTask, "Задача не найдена.");
        assertEquals(subTask1, newTask, "Задачи не совпадают.");

        Long getId1 = (long) 10;
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> taskManager.getSubTaskById(getId1));
        assertEquals("задача с idSubTask:" + getId1 + " отсутствует", ex2.getMessage());

        Long getId = (long) -1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskManager.getSubTaskById(getId));
        assertEquals("idSubTask с отрицательным значением", ex.getMessage());
    }

    @Test
    void updateTaskTest() {
        Task task = new Task();
        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.updateTask(task));
        assertEquals("task = null", ex.getMessage());

        taskManager.addTask(task1);
        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        assertEquals(task1, taskManager.getTaskMap().get(task1.getTaskId()));
    }

    @Test
    void updateSubTaskTest() {
        Subtask subTask = new Subtask();
        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.updateSubTask(subTask));
        assertEquals("subTask = null", ex.getMessage());

        NullPointerException ex1 = assertThrows(NullPointerException.class, () -> taskManager.addSubTask(subTask1));
        assertEquals("idEpicTask = null", ex1.getMessage());

        taskManager.addEpic(epic1);
        subTask1.setEpicId(1L);
        taskManager.addSubTask(subTask1);
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubTaskMap().get(subTask1.getTaskId()));
        assertNotEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    void updateEpicTest() {
        Epic epicTask = new Epic();
        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.updateEpic(epicTask));
        assertEquals("epicTask = null", ex.getMessage());
    }

    @Test
    void deleteTaskTest() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.deleteTask(null));
        assertEquals("id = null", ex.getMessage());

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> taskManager.deleteTask(123L));
        assertEquals("задача с id:" + 123L + " для удаления отсутствует", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> taskManager.deleteTask(-1L));
        assertEquals("id с отрицательным значением", ex2.getMessage());
    }

    @Test
    void getListSubTaskFromEpicTest() {
        taskManager.addEpic(epic1);
        subTask1.setEpicId(epic1.getTaskId());
        subTask2.setEpicId(epic1.getTaskId());
        subTask3.setEpicId(epic1.getTaskId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        assertEquals(3, taskManager.getListSubTaskFromEpic(epic1.getTaskId()).size());

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> taskManager.getListSubTaskFromEpic(-1L));
        assertEquals("эпик с id:" + -1L + " отсутствует", ex1.getMessage());

        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.getListSubTaskFromEpic(null));
        assertEquals("idEpicTask = null", ex.getMessage());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> taskManager.getListSubTaskFromEpic(123L));
        assertEquals("эпик с id:" + 123L + " отсутствует", ex3.getMessage());
    }

    @Test
    void getListTaskTest() {
        assertTrue(taskManager.getListTask().isEmpty(), "Список не пустой");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        assertEquals(3, taskManager.getListTask().size(), "размер ListTask отличается от созданных задач");
    }

    @Test
    void getListEpicTest() {
        assertTrue(taskManager.getListEpic().isEmpty(), "Список не пустой");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        assertEquals(3, taskManager.getListEpic().size(), "размер ListTask отличается от созданных задач");
    }

    @Test
    void GetHistoryTest() {
        assertTrue(taskManager.history().isEmpty(), "Список не пустой");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        subTask1.setEpicId(epic1.getTaskId());
        subTask2.setEpicId(epic1.getTaskId());
        subTask3.setEpicId(epic1.getTaskId());
        subTask4.setEpicId(epic1.getTaskId());
        subTask5.setEpicId(epic2.getTaskId());
        subTask6.setEpicId(epic2.getTaskId());
        subTask7.setEpicId(epic3.getTaskId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);
        taskManager.addSubTask(subTask7);

        taskManager.getTaskById(task1.getTaskId());
        taskManager.getTaskById(task2.getTaskId());
        taskManager.getTaskById(task3.getTaskId());

        taskManager.getEpicById(epic1.getTaskId());
        taskManager.getEpicById(epic2.getTaskId());
        taskManager.getEpicById(epic3.getTaskId());

        taskManager.getSubTaskById(subTask1.getTaskId());
        taskManager.getSubTaskById(subTask2.getTaskId());
        taskManager.getSubTaskById(subTask3.getTaskId());
        taskManager.getSubTaskById(subTask4.getTaskId());
        taskManager.getSubTaskById(subTask5.getTaskId());
        taskManager.getSubTaskById(subTask6.getTaskId());
        taskManager.getSubTaskById(subTask7.getTaskId());

        assertEquals(13, taskManager.getAllTasks().size(), "размер AllTasks() отличается"
                + " от запрошенных задач задач");
        assertEquals(13, taskManager.history().size(), "размер HistoryList() отличается"
                + " от запрошенных задач задач");
    }

    @Test
    void getTaskTest() {
        assertTrue(taskManager.getTaskMap().isEmpty(), "tasks не пустой");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        assertEquals(3, taskManager.getTaskMap().size(), "размер tasks отличается от созданных задач");
    }

    @Test
    void getEpicTest() {
        assertTrue(taskManager.getEpicMap().isEmpty(), "epics не пустой");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        assertEquals(3, taskManager.getEpicMap().size(), "размер epics отличается от созданных задач");

    }

    @Test
    void getSubTasksTest() {
        assertTrue(taskManager.getSubTaskMap().isEmpty(), "subTasks не пустой");
        taskManager.addEpic(epic1);
        subTask1.setEpicId(epic1.getTaskId());
        subTask2.setEpicId(epic1.getTaskId());
        subTask3.setEpicId(epic1.getTaskId());
        subTask4.setEpicId(epic1.getTaskId());
        subTask5.setEpicId(epic1.getTaskId());
        subTask6.setEpicId(epic1.getTaskId());
        subTask7.setEpicId(epic1.getTaskId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);
        taskManager.addSubTask(subTask7);
        assertEquals(7, taskManager.getSubTaskMap().size(), "размер subTasks отличается от созданных задач");
    }

    @Test
    void getAllTasksTest() {
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список не пустой");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        subTask1.setEpicId(epic1.getTaskId());
        subTask2.setEpicId(epic1.getTaskId());
        subTask3.setEpicId(epic1.getTaskId());
        subTask4.setEpicId(epic1.getTaskId());
        subTask5.setEpicId(epic2.getTaskId());
        subTask6.setEpicId(epic2.getTaskId());
        subTask7.setEpicId(epic3.getTaskId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);
        taskManager.addSubTask(subTask7);
        assertEquals(13, taskManager.getAllTasks().size(), "размер AllTasks() отличается от созданных задач");
    }
}
