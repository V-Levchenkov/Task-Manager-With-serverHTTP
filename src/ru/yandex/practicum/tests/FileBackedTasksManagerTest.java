import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managerscollection.FileBackedTasksManager;
import ru.yandex.practicum.tasks.Epic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {


    @BeforeEach
    public void beforeEach() {
        filledManager = new FileBackedTasksManager("tests.csv");
        emptyManager = new FileBackedTasksManager("tests.csv");
        filledManager.addTask(task);
        filledManager.addEpic(epic1);
        filledManager.addSubTask(subTask11, 2);
        filledManager.addSubTask(subTask12, 2);
        filledManager.addEpic(epic2);
        filledManager.addSubTask(subTask21, 5);
    }

    @Test
    void saveWhileEmptyTasksList() {
        emptyManager.save();
        try {
            Assertions.assertEquals(Files.readAllLines(Paths.get("tests.csv")).size(), 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveContainsEpicWithNoSubTasks() {
        emptyManager.addEpic(new Epic("epicName", "epicDescription"));
        emptyManager.save();
        String string = null;
        try {
            string = Files.readAllLines(Paths.get("tests.csv")).get(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(string, "1,EPIC,epicName,NEW,epicDescription,null,null,null");
    }

    @Test
    void saveContainsFilledHistory() {
        emptyManager.addEpic(epic1);
        emptyManager.getEpicById(1);
        emptyManager.save();
        try {
            Assertions.assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(3), "1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void justSaveFilledManager() {
        filledManager.getTaskById(1);
        filledManager.getSubTaskById(3);
        filledManager.getEpicById(2);
        filledManager.save();

        try {
            assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(1),
                    "1,TASK,name,NEW,description,2022-04-17T09:00,PT50M,2022-04-17T09:50");

            assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(2),
                    "3,SUBTASK,subTask11Name,NEW,subTask11Description,2,2022-04-16T10:00,PT1H,2022-04-16T11:00");

            assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(3),
                    "4,SUBTASK,subTask12Name,NEW,subTask12Description,2,2022-04-16T11:00,PT1H,2022-04-16T12:00");

            assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(4),
                    "6,SUBTASK,subTask21Name,DONE,subTask21Description,5,2022-04-16T13:00,PT1H,2022-04-16T14:00");

            assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(5),
                    "2,EPIC,epic1Name,NEW,epic1Description,2022-04-16T10:00,PT2H,2022-04-16T12:00");

            assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(6),
                    "5,EPIC,epic2Name,DONE,epic2Description,2022-04-16T13:00,PT1H,2022-04-16T14:00");

            assertEquals(Files.readAllLines(Paths.get("tests.csv")).get(8), "1,3,2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}