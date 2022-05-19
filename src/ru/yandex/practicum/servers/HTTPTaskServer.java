package ru.yandex.practicum.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.managerscollection.Managers;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;


public class HTTPTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static Gson gson = new Gson();
    private TaskManager taskManager;

    public HTTPTaskServer(TaskManager taskManager) throws IOException, InterruptedException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicTaskHandler());
        httpServer.createContext("/tasks/subtask", new SubTaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gson = gsonBuilder.create();
        this.taskManager = Managers.getDefault();
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " /tasks запроса от клиента.");
            String URL = httpExchange.getRequestURI().toString();
            System.out.println(URL);

            String response = "";
            int responseCode = 200;
            switch (method) {
                case "GET":
                    System.out.println("Вы использовали метод GET!");
                    if (URL.contains("sorted")) {
                        String isSorted = URL.split("=")[1];
                        if (isSorted.equals("true")) {
                            response = gson.toJson(taskManager.getPrioritizedTasks());
                            System.out.println("возврат сортед");
                        } else {
                            response = gson.toJson(taskManager.getAllTasks());
                        }
                    }
                    break;
                case "DELETE":
                    System.out.println("Вы использовали метод DELETE!");
                    taskManager.deleteAll();
                    if (taskManager.getAllTasks().isEmpty() && taskManager.history().isEmpty()) {
                        response = "менеджер очищен";
                    } else {
                        response = "ошибка очищения менеджера";
                    }
                    break;
                default:
                    response = "Такой метод не используется!";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
                os.write(response.getBytes());
            }
        }
    }

    class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " /tasks/history запроса от клиента.");
            String URL = httpExchange.getRequestURI().toString();
            String response = "";
            int responseCode = 200;
            switch (method) {
                case "GET":
                    response = gson.toJson(taskManager.history());
                    break;
                case "DELETE":
                    if (URL.contains("id")) {
                        String id = URL.split("=")[1];
                        taskManager.deleteFromHistory(Long.parseLong(id));
                        if (!taskManager.history().contains(taskManager.getTaskMap().get(Long.parseLong(id)))) {
                            response = "задача удалена из истории";
                        } else {
                            response = "задача не удалена из истории";
                        }

                    } else {
                        responseCode = 400;
                    }
                    break;
                default:
                    response = "Такой метод не используется!";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
                os.write(response.getBytes());
            }
        }
    }


    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " /tasks/task запроса от клиента.");
            String URL = httpExchange.getRequestURI().toString();
            String response = "";
            int responseCode = 200;
            switch (method) {
                case "POST":
                    if (URL.equals("/tasks/task")) {
                        try (InputStream is = httpExchange.getRequestBody()) {
                            String json = new String(is.readAllBytes());
                            Task task = gson.fromJson(json, Task.class);
                            taskManager.addTask(task);
                            if (taskManager.getTaskMap().containsKey(task.getTaskId())) {
                                response = "задача создана";
                            } else {
                                responseCode = 204;
                                response = "ошибка создания task";
                            }
                        }
                    } else {
                        responseCode = 204;
                        response = "ошибка создания task";
                    }
                    break;
                case "GET":
                    if (URL.contains("id")) {
                        Long id = Long.parseLong(URL.split("=")[1]);
                        if (taskManager.getTaskMap().containsKey(id)) {
                            response = gson.toJson(taskManager.getTaskById(id));
                        } else {
                            response = "нет такой задачи";
                        }
                    } else if (URL.equals("/tasks/task")) {
                        response = gson.toJson(taskManager.getListTask());
                    }
                    break;
                case "PUT":
                    if (URL.equals("/tasks/task")) {
                        try (InputStream is = httpExchange.getRequestBody()) {
                            String json = new String(is.readAllBytes());
                            Task task = gson.fromJson(json, Task.class);
                            taskManager.updateTask(task);
                            response = gson.toJson(taskManager.getTaskById(task.getTaskId()));
                        }
                    } else {
                        response = "ошибка изменения задачи";
                    }
                    break;
                case "DELETE":
                    if (URL.contains("id")) {
                        Long id = Long.parseLong(URL.split("=")[1]);
                        if (taskManager.getTaskMap().containsKey(id)) {
                            taskManager.deleteTask(id);
                            response = "задача удалена";
                        } else {
                            responseCode = 404;
                            response = "задача не удалена!";
                        }

                    }
                    break;
                default:
                    response = "Такой метод не используется!";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
                os.write(response.getBytes());
            }
        }
    }
    class EpicTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " /tasks/epic запроса от клиента.");
            String URL = httpExchange.getRequestURI().toString();
            String response = "";
            int responseCode = 200;
            switch (method) {
                case "POST":
                    if (URL.equals("/tasks/epic")) {
                        try (InputStream is = httpExchange.getRequestBody()) {
                            String json = new String(is.readAllBytes());
                            Epic epic = gson.fromJson(json, Epic.class);
                            taskManager.addEpic(epic);
                            if (taskManager.getEpicMap().containsKey(epic.getTaskId())) {
                                response = "задача создана";
                            } else {
                                responseCode = 204;
                                response = "ошибка создания task";
                            }
                        }
                    } else {
                        responseCode = 204;
                        response = "ошибка создания task";
                    }
                    break;
                case "GET":
                    if (URL.contains("id")) {
                        Long id = Long.parseLong(URL.split("=")[1]);
                        if (taskManager.getEpicMap().containsKey(id)) {
                            response = gson.toJson(taskManager.getEpicById(id));
                        } else {
                            response = "нет такой задачи";
                            responseCode = 404;
                        }
                    } else if (URL.equals("/tasks/epic")) {
                        response = gson.toJson(taskManager.getListEpic());
                    }
                    break;
                case "DELETE":
                    if (URL.contains("id")) {
                        Long id = Long.parseLong(URL.split("=")[1]);
                        if (taskManager.getEpicMap().containsKey(id)) {
                            taskManager.deleteEpic(id);
                            response = "задача удалена";
                        } else {
                            responseCode = 404;
                            response = "задача не удалена!";
                        }
                    }
                    break;
                default:
                    response = "Такой метод не используется!";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
                os.write(response.getBytes());
            }
        }
    }

    class SubTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " /tasks/task запроса от клиента.");
            String URL = httpExchange.getRequestURI().toString();
            String response = "";
            int responseCode = 200;
            switch (method) {
                case "POST":
                    if (URL.equals("/tasks/subtask")) {
                        try (InputStream is = httpExchange.getRequestBody()) {
                            String json = new String(is.readAllBytes());
                            Subtask subTask = gson.fromJson(json, Subtask.class);
                            taskManager.addSubTask(subTask);
                            if (taskManager.getSubTaskMap().containsKey(subTask.getTaskId())) {
                                response = "задача создана";
                            } else {
                                responseCode = 204;
                                response = "ошибка создания task";
                            }
                        }
                    } else {
                        responseCode = 204;
                        response = "ошибка создания task";
                    }
                    break;
                case "GET":
                    if (URL.contains("list")) {
                        Long id = Long.parseLong(URL.split("=")[1]);
                        Epic epic = taskManager.getEpicMap().get(id);
                        response = gson.toJson(taskManager.getListSubTaskFromEpic(epic.getTaskId()));
                    } else if (URL.contains("id")) {
                        Long id = Long.parseLong(URL.split("=")[1]);
                        response = gson.toJson(taskManager.getSubTaskById(id));
                    }
                    break;
                case "PUT":
                    if (URL.equals("/tasks/subtask")) {
                        try (InputStream is = httpExchange.getRequestBody()) {
                            String json = new String(is.readAllBytes());
                            Subtask subtask = gson.fromJson(json, Subtask.class);
                            taskManager.updateSubTask(subtask);
                            response = gson.toJson(taskManager.getSubTaskMap().get(subtask.getTaskId()));
                        }
                    } else {
                        response = "ошибка изменения задачи";
                    }
                    break;
                case "DELETE":
                    if (URL.contains("id")) {
                        Long id = Long.parseLong(URL.split("=")[1]);
                        if (taskManager.getSubTaskMap().containsKey(id)) {
                            taskManager.deleteSubTask(id);
                            response = "задача удалена";
                        } else {
                            responseCode = 404;
                            response = "задача не удалена!";
                        }

                    }
                    break;
                default:
                    response = "Такой метод не используется!";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
                os.write(response.getBytes());
            }
        }
    }
}