
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.ManagerSaveException;
import ru.yandex.practicum.managerscollection.InMemoryTaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;


import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

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


    @BeforeEach
    void create() {
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
    }

    @Test
    void emptyHistoryListTest() {
        assertFalse(taskManager.history().isEmpty(), "В списке истории нет задач");
        taskManager.deleteAll();
        assertTrue(taskManager.history().isEmpty(), "В списке истории есть задачи");
    }

    @Test
    void dublicateHistoryTest() {
        int check = 0;
        assertFalse(taskManager.history().isEmpty(), "В списке истории нет задач");
        Task task = taskManager.history().get(0);
        taskManager.getTaskById(task.getTaskId());
        taskManager.getTaskById(task.getTaskId());
        taskManager.getTaskById(task.getTaskId());
        for (Task task1 : taskManager.history()) {
            if (task1.equals(task)) {
                check++;
            }
        }
        assertEquals(1, check, "больше одного раза");
    }

    @Test
    void removeFromHistoryTest() {
        int check1 = taskManager.history().size();
        taskManager.getHistoryManager().remove(taskManager.history().get(0).getTaskId());
        int check2 = taskManager.history().size();
        assertNotEquals(check1,check2,"Одинаковый размер списка истории после удаления");
    }
}