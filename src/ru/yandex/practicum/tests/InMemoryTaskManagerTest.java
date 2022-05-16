
import ru.yandex.practicum.managerscollection.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }
}
