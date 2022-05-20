import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.managerscollection.InMemoryTaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    public void beforeEach() {
        filledManager = new InMemoryTaskManager();
        emptyManager = new InMemoryTaskManager();
        filledManager.addTask(task);
        filledManager.addEpic(epic1);
        filledManager.addSubTask(subTask11, epic1.getTaskId());
        filledManager.addSubTask(subTask12, epic1.getTaskId());
        filledManager.addEpic(epic2);
        filledManager.addSubTask(subTask21, epic2.getTaskId());
        subTask21.setStatus(TaskStatus.DONE);
        filledManager.updateSubTask(subTask21);
    }
}