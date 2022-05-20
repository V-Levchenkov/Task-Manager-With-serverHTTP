package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.managerscollection.exception.ManagerSaveException;
import ru.yandex.practicum.managerscollection.interfaces.HistoryManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskManager;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private long id;
    private final HashMap<Long, Task> Tasks;
    private final HashMap<Long, Epic> Epics;
    private final HashMap<Long, SubTask> SubTasks;
    private final HistoryManager historyManager;
    public Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        Tasks = new HashMap<>();
        Epics = new HashMap<>();
        SubTasks = new HashMap<>();
        id = 0;
        historyManager = Managers.getDefaultHistory();

        Comparator<Task> taskComparator = (o1, o2) -> {
            if (o1.getStartTime() == null && o2.getStartTime() == null) {
                return 0;
            } else if (o1.getStartTime() == null) {
                return -1;
            } else if (o2.getStartTime() == null) {
                return 1;
            } else
                return o1.getStartTime().compareTo(o2.getStartTime());
        };
        prioritizedTasks = new TreeSet<>(taskComparator);
    }

    public HashMap<Long, SubTask> getSubTasksMap() {
        return SubTasks;
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(long id) {
        return (ArrayList<SubTask>) ((Epic)getAllTasksById(id)).getSubtasks();
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public HashMap<Long, Epic> getEpicsMap() {
        return Epics;
    }

    public HashMap<Long, Task> getTasksMap() {
        return Tasks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public ArrayList<Epic> getAllEpicsList() {
        return new ArrayList<>(Epics.values());
    }

    @Override
    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(Tasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasksList() {
        return new ArrayList<>(SubTasks.values());
    }

    //удаление всех задач
    @Override
    public void clearTasks() {
        for (Task task : Tasks.values()) {
            prioritizedTasks.remove(task);
        }
        Tasks.clear();
    }

    @Override
    public void clearEpics() {
        Epics.clear();
        clearSubTasks();
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subTask : SubTasks.values()) {
            prioritizedTasks.remove(subTask);
        }
        SubTasks.clear();
        Epics.clear();
    }


    //получение по идентификатору
    @Override
    public Task getTaskById(long id) {
        if (Tasks.get(id) != null) {
            historyManager.add(Tasks.get(id));
            return Tasks.get(id);
        } else
            return null;
    }

    @Override
    public Task getSubTaskById(long id) {
        if (SubTasks.get(id) != null) {
            historyManager.add(SubTasks.get(id));
            return SubTasks.get(id);
        } else
            return null;
    }


    public Task getEpicById(long id) {
        if (Epics.get(id) != null) {
            historyManager.add(Epics.get(id));
            return Epics.get(id);
        } else
            return null;
    }

    public void addTask(Task task) throws ManagerSaveException {

        if (taskIsValid(task)) {
            id += 1;
            Tasks.put(id, task);
            task.setTaskId(id);
            task.setStatus(task.getStatus());
            prioritizedTasks.add(task);
        }
    }

    public void addSubTask(SubTask subtask, long epicId) throws ManagerSaveException {

        if (taskIsValid(subtask)) {
            id += 1;
            subtask.setEpicId(epicId);
            SubTasks.put(id, subtask);

            subtask.setTaskId(id);
            getEpicsMap().get(epicId).addSubTaskToEpicList(subtask);
            getEpicsMap().get(epicId).refreshEpicData();
            prioritizedTasks.add(subtask);
        }
    }

    public void addEpic(Epic epic) throws ManagerSaveException {
        id += 1;
        Epics.put(id, epic);
        epic.setTaskId(id);
        epic.setStatus(TaskStatus.NEW);
    }

    //Обновление задач.
    public void updateEpic(Epic epic) {
        if (Epics.containsKey(epic.getTaskId())) {
            Epics.put(epic.getTaskId(), epic);
            epic.refreshEpicData();
            epic.refreshEndTime();
        }
    }


    public void updateTask(Task task) {

        if (Tasks.containsKey(task.getTaskId())) {

            Task oldTask = cloneTask(Tasks.get(task.getTaskId()));
            Tasks.remove(oldTask.getTaskId());
            prioritizedTasks.remove(oldTask);
            if (taskIsValid(task)) {
                Tasks.put(task.getTaskId(), task);
                prioritizedTasks.add(task);
            } else {
                Tasks.put(oldTask.getTaskId(), oldTask);
                prioritizedTasks.add(oldTask);
            }
        }
    }

    public void updateSubTask(SubTask newSubtask) {
        if (SubTasks.containsKey(newSubtask.getTaskId())) {
            SubTask oldSubTask = cloneSubTask(SubTasks.get(newSubtask.getTaskId()));
            Epic epic = Epics.get(oldSubTask.getEpicId());
            prioritizedTasks.remove(oldSubTask);
            SubTasks.remove(oldSubTask.getTaskId());

            if (taskIsValid(newSubtask)) {
                SubTasks.put(newSubtask.getTaskId(), newSubtask);
                prioritizedTasks.add(newSubtask);
                if(epic.getSubTasks().contains(oldSubTask)){
                    epic.getSubTasks().remove(oldSubTask);
                    epic.getSubTasks().add(newSubtask);}
                epic.refreshEpicData();
                epic.refreshEndTime();
            } else {
                SubTasks.put(oldSubTask.getTaskId(), oldSubTask);
                Epics.get(oldSubTask.getEpicId()).refreshEpicData();
            }
        }
    }


    //Методы для удаления по ID(Вместе с эпиком удаляются и подзадачи)
    @Override
    public void deleteEpicById(long id) {
        if (Epics.containsKey(id)) {
            for (SubTask subtask : Epics.get(id).getSubTasks()) {
                prioritizedTasks.remove(SubTasks.get(subtask.getTaskId()));
                SubTasks.remove(subtask.getTaskId());
                removeFromHistoryByID(subtask.getTaskId());
            }
            Epics.remove(id);
            removeFromHistoryByID(id);
        }
    }

    @Override
    public void deleteTaskById(long id) {
        if (Tasks.containsKey(id)) {
            prioritizedTasks.remove(Tasks.get(id));
            Tasks.remove(id);
            removeFromHistoryByID(id);
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        if (SubTasks.containsKey(id)) {
            prioritizedTasks.remove(SubTasks.get(id));
            SubTasks.remove(id);
            removeFromHistoryByID(id);
        }
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }


    @Override
    public void removeFromHistoryByID(long id) {
        historyManager.remove(id);
    }


    public Task getAllTasksById(long id) {
        if (Epics.containsKey(id)) {
            return Epics.get(id);
        } else if (Tasks.containsKey(id)) {
            return Tasks.get(id);
        } else return SubTasks.getOrDefault(id, null);
    }

    public boolean taskIsValid(Task checkingTask) {
        long checkingStartTime = ZonedDateTime.of(checkingTask.getStartTime(), ZoneId.systemDefault())
                .toInstant().toEpochMilli();
        long checkingEndTime = ZonedDateTime.of(checkingTask.getEndTime(), ZoneId.systemDefault())
                .toInstant().toEpochMilli();
        long checkingDuration = checkingEndTime - checkingStartTime;

        List<Task> tempList = getPrioritizedTask();
        if (checkingDuration < 0) {
            System.out.println("Задача не добавлена/изменена, так как введенная " +
                    "продолжительность выполнения отрицательная");
            return false;
        } else if (tempList.isEmpty()) {
            return true;
        } else {
            for (Task tempTask : tempList) {
                long tempStartTime = ZonedDateTime.of(tempTask.getStartTime(), ZoneId.systemDefault())
                        .toInstant().toEpochMilli();
                long tempEndTime = ZonedDateTime.of(tempTask.getEndTime(), ZoneId.systemDefault())
                        .toInstant().toEpochMilli();
                if (checkingStartTime >= tempStartTime && checkingStartTime < tempEndTime) {
                    System.out.println("Задача не добавлена/изменена," +
                            " так как время исполнения задачи пересекается с существующей задачей " +
                            tempTask.getTaskName() + " c id " + tempTask.getTaskId());
                    return false;
                } else if (checkingEndTime > tempStartTime && checkingEndTime <= tempEndTime) {
                    System.out.println("Задача не добавлена/изменена," +
                            " так как время исполнения задачи пересекается с существующей задачей " +
                            tempTask.getTaskName() + " c id " + tempTask.getTaskId());
                    return false;
                } else if (checkingStartTime <= tempStartTime && checkingEndTime >= tempEndTime) {
                    System.out.println("Задача не добавлена/изменена," +
                            " так как время исполнения задачи пересекается с существующей задачей " +
                            tempTask.getTaskName() + " c id " + tempTask.getTaskId());
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public ArrayList<Task> getPrioritizedTask() {
        return new ArrayList<Task>(getPrioritizedTasks());
    }



    public Task cloneTask(Task taskToClone) {
        Task newTask = new Task(taskToClone.getTaskName(), taskToClone.getTaskDescription(), taskToClone.getStartTime(), taskToClone.getDuration());
        newTask.setTaskId(taskToClone.getTaskId());
        newTask.setStatus(taskToClone.getStatus());
        return newTask;
    }

    public SubTask cloneSubTask(SubTask subTask) {
        SubTask newSubTask = new SubTask(subTask.getTaskName(), subTask.getTaskDescription(), subTask.getEpicId(), subTask.getStartTime(), subTask.getDuration());
        newSubTask.setTaskId(subTask.getTaskId());
        newSubTask.setStatus(subTask.getStatus());
        return newSubTask;
    }

    public void createTaskWhileLoading(Task task) {

        if (taskIsValid(task)) {
            id = task.getTaskId();
            Tasks.put(id, task);
            task.setTaskId(id);
            // task.setStatus(task.getStatus());
            prioritizedTasks.add(task);
        }
    }

    public void createSubTaskWhileLoading(SubTask subtask) {

        if (taskIsValid(subtask)) {
            id = subtask.getTaskId();
            SubTasks.put(id, subtask);
            subtask.setTaskId(id);
            Epics.get(subtask.getEpicId()).addSubTaskToEpicList(subtask);
            Epics.get(subtask.getEpicId()).refreshEpicData();
            prioritizedTasks.add(subtask);
        }
    }

    public void createEpicWhileLoading(Epic epic) {
        id = epic.getTaskId();
        Epics.put(id, epic);
        epic.setTaskId(id);
        epic.setStatus(epic.getStatus());
    }

}