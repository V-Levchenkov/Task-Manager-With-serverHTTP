import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managerscollection.Managers;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.servers.HttpTaskServer;
import ru.yandex.practicum.servers.KVServer;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private TaskManager manager;
    private HttpClient client = HttpClient.newHttpClient();

    private static Task task;
    private static Epic epic1;
    private static SubTask subTask11;
    private static SubTask subTask12;
    private static Epic epic2;
    private static SubTask subTask21;

    @BeforeEach
    void start() throws IOException, URISyntaxException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }


    @AfterEach
    void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void getTasksWhileEmptyList() {
        String body = sendGetRquest("tasks/task/").body();
        assertEquals(body, "[]");
    }

    @Test
    void getTasksWhileNotEmptyList() {
        String taskJson = "[{\"name\":\"name\",\"description\":\"description\",\"uin\":1,\"status\":\"NEW\"," +
                "\"duration\":{\"seconds\":3000,\"nanos\":0},\"startTime\":{\"date\":{\"year\":2022,\"month\":4," +
                "\"day\":17},\"time\":{\"hour\":9,\"minute\":0,\"second\":0,\"nano\":0}}}]";

        sendPostRequest("task", task);
        assertEquals(sendGetRquest("tasks/task").body(), taskJson);
    }

    @Test
    void getSubTasksWhileEmptyList() {
        assertEquals(sendGetRquest("tasks/subtask/").body(), "[]");
    }

    @Test
    void getSubTasksWhileNotEmptyList() {
        Gson gson = new Gson();
        sendPostRequest("subtask", subTask11);
        String subtaskJson = "[" + gson.toJson(manager.getSubTaskById(1)) + "]";
        assertEquals(sendGetRquest("tasks/subtask/").body(), subtaskJson);
    }

    @Test
    void getEpicsWhileEmptyList() {
        assertEquals(sendGetRquest("tasks/subtask/").body(), "[]");
    }

    @Test
    void getEpicsTaskWhileNotEmptyList() {
        Gson gson = new Gson();
        sendPostRequest("epic", epic1);
        String subtaskJson = "[" + gson.toJson(manager.getEpicById(1)) + "]";
        assertEquals(sendGetRquest("tasks/epic/").body(), subtaskJson);
    }

    @Test
    void getTasksByIdWhileEmptyList() {
        assertEquals(sendGetRquest("tasks/task/?id=1").body(), "");
    }

    @Test
    void getTasksByIdWhileNotEmptyList() {
        sendPostRequest("task", task);
        String string = sendGetRquest("tasks/task/?id=1").body();
        String testString = "{\"name\":\"name\",\"description\":\"description\",\"uin\":1,\"status\":\"NEW\"," +
                "\"duration\":{\"seconds\":3000,\"nanos\":0},\"startTime\":{\"date\":{\"year\":2022,\"month\":4," +
                "\"day\":17},\"time\":{\"hour\":9,\"minute\":0,\"second\":0,\"nano\":0}}}";
        assertEquals(string, testString);

    }

    @Test
    void getSubTasksByIdWhileEmptyList() {
        assertEquals(sendGetRquest("tasks/subtask/?id=1").body(), "");
    }

    @Test
    void getSubTasksByIdWhileNotEmptyList() {
        sendPostRequest("subtask", subTask11);
        String string = sendGetRquest("tasks/subtask/?id=1").body();
        String testString = "{\"epicId\":0,\"name\":\"subTask11Name\",\"description\":\"subTask11Description\"," +
                "\"uin\":1,\"status\":\"NEW\",\"duration\":{\"seconds\":3600,\"nanos\":0}," +
                "\"startTime\":{\"date\":{\"year\":2022,\"month\":4,\"day\":16},\"time\":{\"hour\":10," +
                "\"minute\":0,\"second\":0,\"nano\":0}}}";
        assertEquals(string, testString);
    }

    @Test
    void getEpicsByIdWhileEmptyList() {
        assertEquals(sendGetRquest("tasks/epic/?id=1").body(), "");
    }

    @Test
    void getEpicsByIdWhileNotEmptyList() {
        sendPostRequest("epic", epic1);
        String string = sendGetRquest("tasks/epic/?id=1").body();
        String testString = "{\"subTasks\":[],\"name\":\"epic1Name\",\"description\":\"epic1Description\"," +
                "\"uin\":1,\"status\":\"NEW\"}";
        assertEquals(string, testString);
    }

    @Test
    void getHistoryWhileEmpty() {
        assertEquals(sendGetRquest("tasks/history").body(), "[]");
    }

    @Test
    void getHistory() {
        sendPostRequest("task", task);
        sendPostRequest("epic", epic1);
        sendPostRequest("subtask", subTask11);
        sendGetRquest("tasks/task/?id=1");
        sendGetRquest("tasks/epic/?id=2");
        String jsonOftaskAndEpic = "[{\"name\":\"name\",\"description\":\"description\",\"uin\":1," +
                "\"status\":\"NEW\",\"duration\":{\"seconds\":3000,\"nanos\":0}," +
                "\"startTime\":{\"date\":{\"year\":2022,\"month\":4,\"day\":17},\"time\":{\"hour\":9,\"minute\":0," +
                "\"second\":0,\"nano\":0}}},{\"subTasks\":[],\"name\":\"epic1Name\"," +
                "\"description\":\"epic1Description\",\"uin\":2,\"status\":\"NEW\"}]";
        assertEquals(sendGetRquest("tasks/history").body(), jsonOftaskAndEpic);
    }

    @Test
    void getTasksPriority() {
        sendPostRequest("task", task);
        String testString = "[{\"name\":\"name\",\"description\":\"description\",\"uin\":1,\"status\":\"NEW\"," +
                "\"duration\":{\"seconds\":3000,\"nanos\":0},\"startTime\":{\"date\":{\"year\":2022,\"month\":4," +
                "\"day\":17},\"time\":{\"hour\":9,\"minute\":0,\"second\":0,\"nano\":0}}}]";
        assertEquals(sendGetRquest("tasks").body(), testString);
    }

    @Test
    void getTasksPriorityWhileEmpty() {
        assertEquals(sendGetRquest("tasks").body(), "[]");
    }

    @Test
    void postTask() {
        Task newtask = cloneTask(task);
        newtask.setTaskId(1);
        sendPostRequest("task", task);
        assertEquals(manager.getAllTasksById(1), newtask);
    }

    @Test
    void postEpic() {
        Task newtask = cloneEpic(epic1);
        newtask.setTaskId(1);
        newtask.setStatus(TaskStatus.NEW);
        sendPostRequest("epic", epic1);
        assertEquals(manager.getAllTasksById(1), newtask);
    }

    @Test
    void postSubTask() {
        Task newtask = cloneSubTask(subTask11);
        newtask.setTaskId(1);
        sendPostRequest("subtask", subTask11);
        assertEquals(manager.getAllTasksById(1), newtask);
    }

    @Test
    void deleteTasks() {
        sendPostRequest("task", task);
        sendPostRequest("epic", epic1);
        sendPostRequest("subtask", subTask11);
        deleteRequest("tasks/task");
        assertEquals(sendGetRquest("tasks/task").body(), "[]");

    }

    @Test
    void deleteEpics() {
        sendPostRequest("task", task);
        sendPostRequest("epic", epic1);
        sendPostRequest("subtask", subTask11);
        assertEquals(sendGetRquest("tasks/epic").body(), "[]");
    }

    @Test
    void deleteSubTasks() {
        sendPostRequest("task", task);
        sendPostRequest("epic", epic1);
        sendPostRequest("subtask", subTask11);
        assertEquals(sendGetRquest("tasks/subtask").body(), "[]");
    }

    @Test
    void deleteTask() {
        sendPostRequest("task", task);
        sendPostRequest("epic", epic1);
        sendPostRequest("subtask", subTask11);
        assertEquals(sendGetRquest("tasks/task/?id=1").body(), "[]");
    }

    @Test
    void deleteSubTask() {
        sendPostRequest("task", task);
        sendPostRequest("epic", epic1);
        sendPostRequest("subtask", subTask11);
        assertEquals(sendGetRquest("tasks/subtasktask/?id=3").body(), "[]");
    }

    @Test
    void deleteEpic() {
        sendPostRequest("task", task);
        sendPostRequest("epic", epic1);
        sendPostRequest("subtask", subTask11);
        assertEquals(sendGetRquest("tasks/epic/?id=2").body(), "[]");
    }

    void sendPostRequest(String path, Task newTask) {
        HttpResponse<String> response = null;
        try {
            URI url = URI.create("http://localhost:8080/tasks/" + path);
            Gson gson = new Gson();

            String json = gson.toJson(newTask);

            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

            response = HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(url)
                    .POST(body).build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("");
        }

    }

    HttpResponse<String> sendGetRquest(String path) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + path);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    void deleteRequest(String path) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + path);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    static void fillServer() {
        task = new Task("name", "description",
                LocalDateTime.of(2022, 4, 17, 9, 0), Duration.ofMinutes(50));

        epic1 = new Epic("epic1Name", "epic1Description");

        subTask11 = new SubTask("subTask11Name", "subTask11Description", epic1.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 10, 0), Duration.ofMinutes(60));

        subTask12 = new SubTask("subTask12Name", "subTask12Description", epic1.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 11, 0), Duration.ofMinutes(60));

        epic2 = new Epic("epic2Name", "epic2Description");

        subTask21 = new SubTask("subTask21Name", "subTask21Description", epic2.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 13, 0), Duration.ofMinutes(60));
    }

    //    методы для некоторых тестов
    public SubTask cloneSubTask(SubTask subTask) {
        SubTask newSubTask = new SubTask(subTask.getTaskName(), subTask.getTaskDescription(), subTask.getEpicId(), subTask.getStartTime(), subTask.getDuration());
        newSubTask.setTaskId(subTask.getTaskId());
        newSubTask.setStatus(subTask.getStatus());
        return newSubTask;
    }

    public Task cloneTask(Task taskToClone) {
        Task newTask = new Task(taskToClone.getTaskName(), taskToClone.getTaskDescription(), taskToClone.getStartTime(), taskToClone.getDuration());
        newTask.setTaskId(taskToClone.getTaskId());
        newTask.setStatus(taskToClone.getStatus());
        return newTask;
    }

    public Epic cloneEpic(Epic epicToClone) {
        Epic newEpic = new Epic(epicToClone.getTaskName(), epicToClone.getTaskDescription());
        for (SubTask subtask : epicToClone.getSubTasks()) {
            newEpic.addSubtask(subtask);
        }
        newEpic.setTaskId(epicToClone.getTaskId());
        newEpic.refreshEpicData();
        newEpic.refreshEndTime();
        return newEpic;
    }
}
