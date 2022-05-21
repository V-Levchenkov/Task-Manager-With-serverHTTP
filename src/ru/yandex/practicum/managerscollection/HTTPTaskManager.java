package ru.yandex.practicum.managerscollection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.servers.KvTaskClient;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
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
        for (Task task : history()) {
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
}
