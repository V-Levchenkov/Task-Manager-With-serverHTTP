import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managerscollection.InMemoryHistoryManager;
import ru.yandex.practicum.managerscollection.interfaces.HistoryManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class HistoryManagerTest {
    private Task task;
    private Epic epic;
    private SubTask subTask;
    private HistoryManager manager;


    @BeforeEach
    void createManager() {
        manager = new InMemoryHistoryManager();
        task = new Task("name", "description", LocalDateTime.of(2022, 4, 16, 10, 0), Duration.ofMinutes(50));
        task.setTaskId(1);
        epic = new Epic("epicName", "epicDescription");
        epic.setTaskId(2);
        subTask = new SubTask("subTaskName", "subTaskDescription", epic.getTaskId(), LocalDateTime.of(2022, 4, 16, 10, 50), Duration.ofMinutes(50));
        subTask.setTaskId(3);
    }

    @Test
    void addWhileHistoryEmpty() {
        Assertions.assertEquals(manager.getSize(), 0);
        manager.add(epic);
        Assertions.assertEquals(manager.getSize(), 1);
        Assertions.assertEquals(manager.getHistory().get(0).getTaskId(), 2);
    }

    @Test
    void addDuplication() {
        manager.add(task);
        manager.add(task);
        Assertions.assertEquals(manager.getSize(), 1);
        Assertions.assertEquals(manager.getHistory().get(0), task);
    }

    @Test
    void removeFirst() {
        manager.add(task);
        manager.add(subTask);
        manager.remove(task.getTaskId());
        Assertions.assertEquals(manager.getHistory().get(0), subTask);
        Assertions.assertEquals(manager.getHistory().size(), 1);

    }

    @Test
    void removeMiddle() {
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        Assertions.assertEquals(manager.getHistory().size(), 3);
        manager.remove(subTask.getTaskId());
        Assertions.assertEquals(manager.getHistory().get(1), task);
        Assertions.assertEquals(manager.getHistory().size(), 2);
    }

    @Test
    void removeNonExistent() {
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        Assertions.assertEquals(manager.getSize(), 3);
        manager.remove(100500);
        Assertions.assertEquals(manager.getSize(), 3);
    }

    @Test
    void removeLast() {
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        Assertions.assertEquals(manager.getHistory().size(), 3);
        manager.remove(task.getTaskId());
        Assertions.assertEquals(manager.getHistory().size(), 2);
    }

    @Test
    void getTasksTest() {
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        Assertions.assertEquals(manager.getHistory().size(), 3);
        Assertions.assertEquals(manager.getHistory().get(0), epic);
        Assertions.assertEquals(manager.getHistory().get(1), subTask);
        Assertions.assertEquals(manager.getHistory().get(2), task);
    }

    @Test
    void getTasksWhileHistoryIsEmpty() {
        Assertions.assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    void getSizeIfManagerEmpty() {
        Assertions.assertEquals(manager.getSize(), 0);
    }

    @Test
    void getSizeIfManagerFilled() {
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        Assertions.assertEquals(manager.getSize(), 3);
    }
}