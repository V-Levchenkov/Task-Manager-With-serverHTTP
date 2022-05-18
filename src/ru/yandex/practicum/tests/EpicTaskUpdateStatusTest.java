import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managerscollection.InMemoryTaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;


import static org.junit.jupiter.api.Assertions.*;

class EpicTaskUpdateStatusTest {
    private static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static final Epic epic1 = new Epic("epic1", "desc");
    private final Subtask subTask1 = new Subtask("sub1", "desc", TaskStatus.NEW, epic1.getTaskId());
    private final Subtask subTask2 = new Subtask("sub2", "desc", TaskStatus.DONE, epic1.getTaskId());
    private final Subtask subTask3 = new Subtask("sub3", "desc", TaskStatus.IN_PROGRESS, epic1.getTaskId());
    private final Subtask subTask4 = new Subtask("sub4", "desc", TaskStatus.NEW, epic1.getTaskId());

    @BeforeAll
    static void emptyListIdSubTaskTest() {
        inMemoryTaskManager.addEpic(epic1);
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    public void newStatusEpicTest() {
        inMemoryTaskManager.addSubTask(subTask1);
        assertEquals(TaskStatus.NEW, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask1.getTaskId());
    }

    @Test
    public void inProgressStatusEpicTest() {
        inMemoryTaskManager.addSubTask(subTask3);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask3.getTaskId());
    }

    @Test
    public void doneStatusEpicTest() {
        inMemoryTaskManager.addSubTask(subTask2);
        assertEquals(TaskStatus.DONE, epic1.getStatus());
        inMemoryTaskManager.addSubTask(subTask1);
        subTask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubTask(subTask1);
        assertEquals(TaskStatus.DONE, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask2.getTaskId());
        inMemoryTaskManager.deleteSubTask(subTask1.getTaskId());
    }

    @Test
    public void doneStatusEpicTest2() {
        inMemoryTaskManager.addSubTask(subTask1);
        inMemoryTaskManager.addSubTask(subTask2);
        inMemoryTaskManager.addSubTask(subTask3);
        inMemoryTaskManager.addSubTask(subTask4);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask1.getTaskId());
        inMemoryTaskManager.deleteSubTask(subTask3.getTaskId());
        inMemoryTaskManager.deleteSubTask(subTask4.getTaskId());
        assertEquals(TaskStatus.DONE, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask2.getTaskId());
    }

    @Test
    public void inProgressStatusEpicTest3() {
        inMemoryTaskManager.addSubTask(subTask1);
        inMemoryTaskManager.addSubTask(subTask2);
        inMemoryTaskManager.addSubTask(subTask3);
        inMemoryTaskManager.addSubTask(subTask4);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask3.getTaskId());
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask1.getTaskId());
        inMemoryTaskManager.deleteSubTask(subTask2.getTaskId());
        inMemoryTaskManager.deleteSubTask(subTask4.getTaskId());
    }

    @Test
    public void newStatusEpicTest2() {
        inMemoryTaskManager.addSubTask(subTask1);
        inMemoryTaskManager.addSubTask(subTask2);
        inMemoryTaskManager.addSubTask(subTask3);
        inMemoryTaskManager.addSubTask(subTask4);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask3.getTaskId());
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask2.getTaskId());
        assertEquals(TaskStatus.NEW, epic1.getStatus());
        inMemoryTaskManager.deleteSubTask(subTask1.getTaskId());
        inMemoryTaskManager.deleteSubTask(subTask4.getTaskId());
    }
}