import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managerscollection.HTTPTaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.servers.HTTPTaskServer;
import ru.yandex.practicum.servers.KVServer;
import ru.yandex.practicum.servers.serializers.*;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private KVServer kvServer;
    private HTTPTaskServer taskServer;
    private HTTPTaskManager taskManager;
    private final String URL = "http://localhost:8080/tasks/";
    private String task1 = "{\"id\":\"null\",\"title\":\"task 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
            "\"startTime\":\"null\",\"duration\":\"null\"}";
    private String task2 = "{\"id\":\"null\",\"title\":\"task 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
            "\"startTime\":\"null\",\"duration\":\"null\"}";
    private String epic1 = "{\"id\":0,\"title\":\"epic 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
            "\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[]}";
    private String epic2 = "{\"id\":\"null\",\"title\":\"epic 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
            "\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[]}";
    private String subtask1 = "{\"id\":\"null\",\"title\":\"subtask 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
            "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
    private String subtask2 = "{\"id\":\"null\",\"title\":\"subtask 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
            "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
    private Gson toGson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .create();
    private Gson fromGson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Epic.class, new EpicDeserializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .create();


    public void initializeTaskManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HTTPTaskManager(new File("http://localhost:8080"));
    }

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        initializeTaskManager();
        taskServer = new HTTPTaskServer(taskManager);
    }

    @AfterEach
    public void AfterEach() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    public void postGetAndDeleteSimpleTaskInHttpTaskServerTest() {
        postMethod(URL + "task/", task1);
        postMethod(URL + "task/", task2);
        String responseTask1 = "{\"id\":0,\"title\":\"task 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\"}";
        String responseTask2 = "[{\"id\":1,\"title\":\"task 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\"}]";
        assertEquals(getMethod(URL + "task/?id=0"), responseTask1);
        assertEquals(deleteMethod(URL + "task/?id=0"), "Задача id=0 успешно удалена");
        assertEquals(getMethod(URL), responseTask2);

        deleteMethod(URL);
        assertEquals(getMethod(URL), "Список задач пуст");
    }

    @Test
    public void postGetAndDeleteEpicInHttpTaskServerTest() {
        postMethod(URL + "epic", epic1);
        postMethod(URL + "epic", epic2);
        String responseEpic1 = "{\"id\":0,\"title\":\"epic 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[]}";
        String responseEpic2 = "{\"id\":1,\"title\":\"epic 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[]}";
        assertEquals(getMethod(URL), "Список задач пуст");
        assertEquals(getMethod(URL + "epic/?id=0"), responseEpic1);
        assertEquals(deleteMethod(URL + "epic/?id=0"), "Эпик id=0 успешно удалён");
        assertEquals(getMethod(URL + "epic/?id=1"), responseEpic2);
        deleteMethod(URL);
        assertEquals(getMethod(URL + "epic/?id=1"), "Произошла ошибка во время получения эпика");
    }

    @Test
    public void getSubtasksListInEpicInHttpTaskServerTest() {
        String responseSubtask1 = "{\"id\":2,\"title\":\"subtask 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
        postMethod(URL + "epic", epic1);
        postMethod(URL + "epic", epic2);
        assertEquals(getMethod(URL + "epic/subtasks/?id=0"), "У эпика id=0 нет подзадач");
        postMethod(URL + "subtask", subtask1);
        assertEquals(getMethod(URL + "epic/subtasks/?id=0"), "[" + responseSubtask1 + "]");
        assertEquals(getMethod(URL + "epic/subtasks/?id=1"), "У эпика id=1 нет подзадач");
    }

    @Test
    public void postGetAndDeleteSubtaskInHttpTaskServerTest() {
        postMethod(URL + "epic", epic1);
        postMethod(URL + "epic", epic2);
        postMethod(URL + "subtask", subtask1);
        postMethod(URL + "subtask", subtask2);
        String responseEpic1 = "{\"id\":0,\"title\":\"epic 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[2,3]}";
        String responseEpic2 = "{\"id\":1,\"title\":\"epic 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[]}";
        String responseSubtask1 = "{\"id\":2,\"title\":\"subtask 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
        String responseSubtask2 = "{\"id\":3,\"title\":\"subtask 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
        assertEquals(getMethod(URL + "subtask/?id=2"), responseSubtask1);
        assertEquals(getMethod(URL + "subtask/?id=3"), responseSubtask2);
        assertEquals(getMethod(URL + "subtask/?id=4"), "Произошла ошибка во время получения подзадачи");
        assertEquals(getMethod(URL + "epic/?id=0"), responseEpic1);
        assertEquals(deleteMethod(URL + "epic/?id=0"), "Эпик id=0 успешно удалён");
        assertEquals(getMethod(URL + "epic/?id=1"), responseEpic2);
        deleteMethod(URL);
        assertEquals(getMethod(URL + "epic/?id=1"), "Произошла ошибка во время получения эпика");
    }

    @Test
    public void postGetDeleteAndUpdateDifferentTaskInHttpTaskServerTest() {
        postMethod(URL + "epic", epic1);
        postMethod(URL + "epic", epic2);
        postMethod(URL + "subtask", subtask1);
        postMethod(URL + "subtask", subtask2);
        String responseEpic1 = "{\"id\":0,\"title\":\"epic 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[2,3]}";
        String responseSubtask1 = "{\"id\":2,\"title\":\"subtask 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
        String responseSubtask2 = "{\"id\":3,\"title\":\"subtask 2\",\"description\":\"desc 2\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
        String subtask3 = "{\"id\":2,\"title\":\"subtask 1\",\"description\":\"desc 1\"," +
                "\"status\":\"IN_PROGRESS\",\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
        String subtask4 = "{\"id\":3,\"title\":\"subtask 2\",\"description\":\"desc 2\",\"status\":\"DONE\"," +
                "\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}";
        String task3 = "{\"id\":4,\"title\":\"task 1\",\"description\":\"desc 1\",\"status\":\"DONE\"," +
                "\"startTime\":\"null\",\"duration\":\"null\"}";
        String responseTask1 = "{\"id\":4,\"title\":\"task 1\",\"description\":\"desc 1\",\"status\":\"NEW\"," +
                "\"startTime\":\"null\",\"duration\":\"null\"}";
        String responseTask2 = "{\"id\":4,\"title\":\"task 1\",\"description\":\"desc 1\",\"status\":\"DONE\"," +
                "\"startTime\":\"null\",\"duration\":\"null\"}";
        assertEquals(getMethod(URL + "subtask/?id=2"), responseSubtask1);
        assertEquals(getMethod(URL + "subtask/?id=3"), responseSubtask2);
        assertEquals(getMethod(URL + "epic/?id=0"), responseEpic1);
        assertEquals(taskManager.getEpicById(0L).getStatus(), TaskStatus.NEW);
        postMethod(URL + "subtask/?id=2", subtask3);
        assertEquals(taskManager.getEpicById(0L).getStatus(), TaskStatus.IN_PROGRESS);
        postMethod(URL + "subtask/?id=3", subtask4);
        assertEquals(taskManager.getEpicById(0L).getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(taskManager.getSubTaskById(2L).getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(taskManager.getSubTaskById(3L).getStatus(), TaskStatus.DONE);
        deleteMethod(URL + "subtask/?id=2");
        assertEquals(taskManager.getEpicById(0L).getStatus(), TaskStatus.DONE);
        deleteMethod(URL + "subtask/?id=3");
        assertEquals(taskManager.getEpicById(0L).getStatus(), TaskStatus.NEW);
        postMethod(URL + "task/", task1);
        assertEquals(getMethod(URL + "task/?id=4"), responseTask1);
        postMethod(URL + "task/?id=4", task3);
        assertEquals(getMethod(URL + "task/?id=4"), responseTask2);
    }

    @Test
    public void getHistoryInHttpTaskServerTest() {
        postMethod(URL + "epic", epic1);
        postMethod(URL + "epic", epic2);
        postMethod(URL + "subtask", subtask1);
        postMethod(URL + "subtask", subtask2);
        postMethod(URL + "task/", task1);
        postMethod(URL + "task/", task2);
        assertEquals(getMethod(URL + "history"), "История просмотров пуста");
        getMethod(URL + "subtask/?id=2");
        getMethod(URL + "epic/?id=0");
        assertEquals(getMethod(URL + "history"), "{\"id\":2,\"title\":\"subtask 1\",\"description\":\"desc 1\"," +
                "\"status\":\"NEW\",\"startTime\":\"null\",\"duration\":\"null\",\"epicId\":0}{\"id\":0," +
                "\"title\":\"epic 1\",\"description\":\"desc 1\",\"status\":\"NEW\",\"startTime\":\"null\"," +
                "\"endTime\":\"null\",\"subtasks\":[2,3]}");
        deleteMethod(URL + "epic/?id=0");
        getMethod(URL + "epic/?id=1");
        getMethod(URL + "task/?id=5");
        getMethod(URL + "task/?id=6");
        assertEquals(getMethod(URL + "history"), "{\"id\":1,\"title\":\"epic 2\",\"description\":\"desc 2\"," +
                "\"status\":\"NEW\",\"startTime\":\"null\",\"endTime\":\"null\",\"subtasks\":[]}{\"id\":5," +
                "\"title\":\"task 2\",\"description\":\"desc 2\",\"status\":\"NEW\",\"startTime\":\"null\"," +
                "\"duration\":\"null\"}");
    }

    private String deleteMethod(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return response.body();
    }

    private String getMethod(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        assert response != null;
        return response.body();
    }

    private void postMethod(String url, String jsonString) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

}