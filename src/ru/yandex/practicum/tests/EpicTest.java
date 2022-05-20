import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managerscollection.InMemoryTaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();

    @BeforeEach
    void createTasks() {
        Epic epc1 = new Epic("epc1", "this is epic N1");
        Epic epc2 = new Epic("epc2", "this is epic N2");
        Epic epc3 = new Epic("epc3", "this is epic3");
        Epic epc4 = new Epic("epc4", "this is epic4");
        Epic epc5 = new Epic("epc5", "this is epic5");

        SubTask sbt1 = new SubTask("sbt1", "this is subtask1", epc1.getTaskId(), LocalDateTime.of(2023, 4, 17, 11, 0), Duration.ofMinutes(60));
        SubTask sbt2 = new SubTask("sbt2", "this is subtask2", epc1.getTaskId(), LocalDateTime.of(2023, 4, 17, 13, 0), Duration.ofMinutes(60));
        SubTask sbt3 = new SubTask("sbt3", "this is subtask3", epc2.getTaskId(), LocalDateTime.of(2023, 4, 17, 15, 0), Duration.ofMinutes(60));
        SubTask sbt4 = new SubTask("sbt4", "this is subtask4", epc2.getTaskId(), LocalDateTime.of(2023, 4, 17, 17, 0), Duration.ofMinutes(60));
        SubTask sbt5 = new SubTask("sbt5", "this is subtask5", epc4.getTaskId(), LocalDateTime.of(2023, 4, 17, 19, 0), Duration.ofMinutes(60));
        SubTask sbt6 = new SubTask("sbt6", "this is subtask6", epc4.getTaskId(), LocalDateTime.of(2023, 4, 17, 21, 0), Duration.ofMinutes(60));
        SubTask sbt7 = new SubTask("sbt7", "this is subtask7", epc5.getTaskId(), LocalDateTime.of(2023, 4, 17, 23, 0), Duration.ofMinutes(60));
        SubTask sbt8 = new SubTask("sbt8", "this is subtask8", epc5.getTaskId(), LocalDateTime.of(2023, 4, 17, 9, 0), Duration.ofMinutes(60));

        manager.addEpic(epc1); // id = 1
        manager.addEpic(epc2); // id = 2
        manager.addEpic(epc3); // id = 3
        manager.addSubTask(sbt1, epc1.getTaskId()); // id = 4
        manager.addSubTask(sbt2, epc1.getTaskId()); // id = 5
        sbt3.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(sbt3);
        sbt4.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(sbt4);
        manager.addSubTask(sbt3, epc2.getTaskId()); // id = 6
        manager.addSubTask(sbt4, epc2.getTaskId()); // id = 7
        manager.addEpic(epc4); // id = 8
        sbt5.setStatus(TaskStatus.DONE);
        sbt6.setStatus(TaskStatus.DONE);
        manager.updateSubTask(sbt5);
        manager.updateSubTask(sbt6);
        manager.addSubTask(sbt5, epc4.getTaskId()); //id = 9
        manager.addSubTask(sbt6, epc4.getTaskId());// id = 10
        manager.addEpic(epc5); // id = 11
        sbt8.setStatus(TaskStatus.DONE);
        manager.updateSubTask(sbt8);
        manager.addSubTask(sbt7, epc5.getTaskId());// id = 12
        manager.addSubTask(sbt8, epc5.getTaskId()); // id = 13
    }


    @Test
    void calculateEpicStatusIfNoSubtasks() {
        Task epic = manager.getAllTasksById(3);
        assertEquals(epic.getStatus(), TaskStatus.NEW);
    }

    @Test
    void calculateEpicStatusIfAllSubtasksNew() {
        Task epic = manager.getAllTasksById(1);
        assertEquals(epic.getStatus(), TaskStatus.NEW);
    }

    @Test
    void calculateEpicStatusIfAllSubtasksDone() {
        Task epic = manager.getAllTasksById(8);
        assertEquals(epic.getStatus(), TaskStatus.DONE);
    }

    @Test
    void calculateEpicStatusIfSubtasksNewAndDone() {
        Task epic = manager.getAllTasksById(11);
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void calculateEpicStatusIfSubTasksInProgress() {
        assertEquals(manager.getAllTasksById(2).getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void getSubTasksWhileSubTasksListFilled() {
        Epic epic = (Epic) manager.getEpicById(1);
        assertEquals(epic.getSubtasks().size(), 2);
    }

    @Test
    void getSubTasksWhileSubTasksListIsEmpty() {
        Epic epic = (Epic) manager.getEpicById(3);
        assertEquals(epic.getSubtasks().size(), 0);
    }
}