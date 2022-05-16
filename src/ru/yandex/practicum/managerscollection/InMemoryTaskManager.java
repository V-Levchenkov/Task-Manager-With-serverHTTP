package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.exception.ManagerSaveException;
import ru.yandex.practicum.managerscollection.interfaces.HistoryManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.time.Duration;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {


    private long id;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final HashMap<Long, Task> tasks = new HashMap<>();
    private final HashMap<Long, Epic> epics = new HashMap<>();
    private final HashMap<Long, Subtask> subtasks = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Task::compareTo);

    @Override
    public List<Task> history() {  // метод history
        List<Task> historyList;
        historyList = historyManager.getHistory();
        return historyList;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean checkAddTime(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        Task higher = prioritizedTasks.ceiling(task);
        Task lower = prioritizedTasks.floor(task);
        try {
            if (lower != null && lower.getEndTime().isAfter(task.getStartTime())) {
                return false;
            }
            if (higher != null && task.getEndTime().isAfter(higher.getStartTime())) {
                return false;
            }
        } catch (NullPointerException ignored) {
        }
        return true;
    }

    private void setEpicTime(Epic epic) {  //----расчёт startTime,endTime,duration
        TreeSet<Task> subtaskInEpic = new TreeSet<>();
        Subtask subtask = new Subtask();
        if (!epic.getSubTaskId().isEmpty()) {
            for (Long id : epic.getSubTaskId()) {
                subtask = subtasks.get(id);
                if ((subtask.getStartTime()) != null) {
                    subtaskInEpic.add(subtask);
                }
            }
            if (!subtaskInEpic.isEmpty()) {
                Duration duration = Duration.between(subtaskInEpic.first().getStartTime(), subtaskInEpic.last().getEndTime());
                int durationEpicTask = (int) duration.toMinutes();
                epic.setStartTime(subtaskInEpic.first().getStartTime());
                epic.setDuration(durationEpicTask);
                epic.setEpicEndTime(subtaskInEpic.last().getEndTime());
            }
        } else {
            epic.setStartTime(null);
            epic.setDuration(0);
            epic.setEpicEndTime(null);
        }
    }

    public void addInDateList(Task task) {
        if (checkAddTime(task)) {
            prioritizedTasks.add(task);
        }
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public long addId() {
        id++;
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public HashMap<Long, Task> getTaskMap() {
        return tasks;
    }

    @Override
    public void deleteAll() {
        for (Task task : getListTask()) {
            if (history().contains(task)) {
                historyManager.remove(task.getTaskId());
            }
        }
        for (Task task : getListEpic()) {
            if (history().contains(task)) {
                historyManager.remove(task.getTaskId());
            }
        }
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public HashMap<Long, Epic> getEpicMap() {
        return epics;
    }

    @Override
    public HashMap<Long, Subtask> getSubTaskMap() {
        return subtasks;
    }

    @Override
    public void addTask(Task task) throws ManagerSaveException {
        if (task != null) {
            if (task.getClass().equals(Task.class)) {
                task.setTaskId(addId());
                addInDateList(task);
                tasks.put(task.getTaskId(), task);
            } else {
                throw new IllegalArgumentException("задача не соответствует типу Task");
            }
        } else {
            throw new NullPointerException("переданный task = null");
        }
    }

    @Override
    public void addEpic(Epic epic) throws ManagerSaveException {
        if (epic != null) {
            if (epic.getClass().equals(Epic.class)) {
                epic.setTaskId(addId());
                epics.put(epic.getTaskId(), epic);
            } else {
                throw new IllegalArgumentException("задача не соответствует типу Epic");
            }
        } else {
            throw new NullPointerException("переданный epic = null");
        }
    }

    @Override
    public void addSubTask(Subtask subtask) throws ManagerSaveException {
        if (subtask != null) {
            if (subtask.getClass().equals(Subtask.class)) {
                if (epics.containsKey(subtask.getEpicId())) {
                    subtask.setTaskId(addId());
                    addInDateList(subtask);
                    subtasks.put(subtask.getTaskId(), subtask);
                    Epic epic = epics.get(subtask.getEpicId());
                    epic.setIdSubTasks(subtask.getTaskId());
                    setEpicTime(epic);
                    updateEpic(epic);
                } else {
                    throw new IllegalArgumentException("id Epic записанный в subtask отсутствует в хранилище");
                }
            } else {
                throw new IllegalArgumentException("задача не соответствует типу Subtask");
            }
        } else {
            throw new NullPointerException("переданный subtask = null");
        }
    }

    @Override
    public Task getTaskById(Long id) throws ManagerSaveException {
        Task task = new Task();
        if (tasks.containsKey(id)) {
            task = tasks.get(id);
            historyManager.add(task); //добавление задачи в список истории просмотров
        } else {
            System.out.println("задача с id:" + id + " отсутствует");
        }
        return task;
    }

    @Override
    public Epic getEpicById(Long id) throws ManagerSaveException {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubTaskById(Long id) throws ManagerSaveException {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteTask(Long id) throws ManagerSaveException {
        Task task = tasks.get(id);
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(Long id) throws ManagerSaveException {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (long idSubTask : epic.getSubTaskId()) {
                subtasks.remove(idSubTask);
                historyManager.remove(idSubTask);
            }
            epic.getSubTaskId().clear();
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubTask(Long id) throws ManagerSaveException {
        historyManager.remove(id);
        subtasks.remove(id);
    }

    public void updateTask(Task task) throws ManagerSaveException {
        if (tasks.containsKey(task.getTaskId())) {
            tasks.put(task.getTaskId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            ArrayList<String> statusDone = new ArrayList<>();
            ArrayList<String> statusNew = new ArrayList<>();
            for (long id : epic.getSubTaskId()) {
                Subtask subTask = subtasks.get(id);
                if (epic.getSubTaskId().isEmpty()) {
                    epic.setStatus(TaskStatus.NEW);
                } else if (subTask.getStatus().equals(TaskStatus.NEW)) {
                    statusNew.add("New");
                    if (statusNew.size() == epic.getSubTaskId().size()) {
                        epic.setStatus(TaskStatus.NEW);
                    }
                } else if (subTask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                } else if (subTask.getStatus().equals(TaskStatus.DONE)) {
                    statusDone.add("Done");
                    if (statusDone.size() == epic.getSubTaskId().size()) {
                        epic.setStatus(TaskStatus.DONE);
                    }
                } else epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            System.out.println("Такого Epic нет");
        }
    }

    public void updateSubTask(Subtask subtask) throws ManagerSaveException {
        if (subtasks.containsKey(subtask.getTaskId())) {
            subtasks.put(subtask.getTaskId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpic(epic);
        } else {
            System.out.println("Такого Subtask нет");
        }
    }

    @Override
    public ArrayList<Subtask> getListSubTaskFromEpic(Long idEpicTask) {
        if (idEpicTask != null) {
            if (epics.containsKey(idEpicTask)) {
                ArrayList<Subtask> listSubTask = new ArrayList<>();
                Epic epic = epics.get(idEpicTask);
                for (long x : epic.getSubTaskId()) {
                    listSubTask.add(subtasks.get(x));
                }
                return listSubTask;
            } else {
                throw new IllegalArgumentException("эпик с id:" + idEpicTask + " отсутствует");
            }
        } else {
            throw new NullPointerException("idEpicTask = null");
        }
    }

    @Override
    public ArrayList<Task> getListTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getListEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }
}