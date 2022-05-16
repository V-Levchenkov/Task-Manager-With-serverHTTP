
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubtaskTest {
    private final Epic epic = new Epic("epicTask1", "desc");
    private final Subtask subTask = new Subtask("subTask1", "desc", epic.getTaskId());

    @Test
    public void setEpicIdTest() {
        NullPointerException ex2 = assertThrows(NullPointerException.class, () -> subTask.setEpicId(null));
        assertEquals("переданный epicId = null" + "l", ex2.getMessage());
        Long q = (long) -1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> subTask.setEpicId(q));
        assertEquals("epicId имеет отрицательное значение", ex.getMessage());
    }

    @Test
    public void getEpicIdTest() {
        NullPointerException ex1 = assertThrows(NullPointerException.class, subTask::getEpicId);
        assertEquals("epicId = null", ex1.getMessage());

    }
}
