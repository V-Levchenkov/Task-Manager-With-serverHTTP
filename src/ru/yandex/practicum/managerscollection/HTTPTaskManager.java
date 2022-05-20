package ru.yandex.practicum.managerscollection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.servers.KVServer;
import ru.yandex.practicum.servers.KvTaskClient;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private KvTaskClient client;
    private Gson gson;
    public HTTPTaskManager(URI url) {
        client = new KvTaskClient(url);
    }


    @Override
    public void save() {
        gson = new Gson();
        List<Long> historyList = new ArrayList<>();
        for(Task task:history()){
            historyList.add(task.getTaskId());
        }

        String tasks = gson.toJson(getAllTasksList());
        String subTasks = gson.toJson(getAllSubTasksList());
        String epics = gson.toJson(getAllEpicsList());
        String history = gson.toJson(historyList);

        try {
            client.put("tasksKey", tasks);
            client.put("epicsKey", epics);
            client.put("subTasksKey", subTasks);
            client.put("historyKey", history);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void createTaskWhileLoad(Task task) {
        if (taskIsValid(task)) {
            getTasksMap().put(task.getTaskId(), task);
            prioritizedTasks.add(task);
        }
    }

    private void createSubTaskWhileLoad(SubTask subtask) {
        if (taskIsValid(subtask)) {
            getSubTasksMap().put(subtask.getTaskId(), subtask);
            getEpicsMap().get(subtask.getEpicId()).refreshEpicData();
            prioritizedTasks.add(subtask);
        }
    }

    private void createEpicWhileLoad(Epic epic) {
        getEpicsMap().put(epic.getTaskId(), epic);
    }

    private static HTTPTaskManager loadFromClient(KvTaskClient client) {

        HTTPTaskManager loadedManager = new HTTPTaskManager(client.getUrl());

        Type TaskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = new Gson().fromJson(client.load("tasksKey"), TaskListType);

        Type EpicListType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> epics = new Gson().fromJson(client.load("epicsKey"), EpicListType);

        Type SubTaskListType = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = new Gson().fromJson(client.load("subTasksKey"), SubTaskListType);

        Type historylistType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        List<Integer> history = new Gson().fromJson(client.load("historyKey"), historylistType);

        for (Task task : tasks) {
            loadedManager.createTaskWhileLoad(task);
        }
        for (Epic epic : epics) {
            loadedManager.createEpicWhileLoad(epic);
        }
        for (SubTask subTask : subTasks) {
            loadedManager.createSubTaskWhileLoad(subTask);
        }
        loadedManager.setId(loadedManager.getEpicsMap().size() + loadedManager.getTasksMap().size()
                + loadedManager.getSubTasksMap().size());
        for (int i = 0; i < history.size(); i++) {
            loadedManager.getHistoryManager().add(loadedManager.getAllTasksById(history.get(i)));
        }
        return loadedManager;
    }
    public static void main(String[] args) {
        try {
            new KVServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HTTPTaskManager manager = null;
        manager = (HTTPTaskManager) Managers.getDefault();
        Epic epc1 = new Epic("epc1", "this is epic N1");
        Epic epc2 = new Epic("epc2", "this is epic N2");
        manager.addEpic(epc1);
        manager.addEpic(epc2);
        SubTask sbt1 = new SubTask("sbt1", "this is subtask1", epc1.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 10, 0), Duration.ofMinutes(60));

        SubTask sbt2 = new SubTask("sbt2", "this is subtask2", epc1.getTaskId(),
                LocalDateTime.of(2022,4,16,11,0), Duration.ofMinutes(60));

        SubTask sbt3 = new SubTask("sbt3", "this is subtask3", epc2.getTaskId(),
                LocalDateTime.of(2022, 4,16,12,0), Duration.ofMinutes(60));

        SubTask sbt4 = new SubTask("sbt4", "this is subtask4", epc2.getTaskId(),
                LocalDateTime.of(2022,4,16,13,0), Duration.ofMinutes(60));


        manager.addSubTask(sbt1, sbt1.getEpicId());
        manager.addSubTask(sbt2, sbt2.getEpicId());
        manager.addSubTask(sbt3, sbt3.getEpicId());
        manager.addSubTask(sbt4, sbt4.getEpicId());

        manager.getEpicById(epc1.getTaskId());
        manager.getEpicById(epc2.getTaskId());
        manager.getSubTaskById(sbt4.getTaskId());
        manager.getSubTaskById(sbt3.getTaskId());
        manager.getSubTaskById(sbt2.getTaskId());
        manager.getSubTaskById(sbt1.getTaskId());

        Task task = new Task("Taskname", "Taskdescription");
        task.setStartTime(LocalDateTime.of(2024,2,3,12,0));
        task.setStatus(TaskStatus.DONE);
        task.setDuration(Duration.ofMinutes(60));
        manager.addTask(task);
        manager.updateTask(task);
        manager.getTaskById(7);


        Task task2 = new Task("Taskname2", "Taskdescription2");
        task2.setStartTime(LocalDateTime.of(2055,11,3,12,0));
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setDuration(Duration.ofMinutes(120));
        manager.addTask(task2);
        manager.updateTask(task2);
        manager.getTaskById(8);

        System.out.println(manager.client.load("tasksKey"));
        System.out.println(manager.client.load("epicsKey"));
        System.out.println(manager.client.load("subTasksKey"));
        System.out.println(manager.client.load("historyKey"));

        HTTPTaskManager newManager = loadFromClient(manager.client);
        String firstManager = manager.getHistoryManager().getHistory().toString();
        String secondManager = newManager.getHistoryManager().getHistory().toString();
        System.out.println(firstManager.equals(secondManager));
        System.out.println(firstManager);
        System.out.println(secondManager);
    }

}
