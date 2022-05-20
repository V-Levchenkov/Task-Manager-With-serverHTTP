package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.managerscollection.exception.ManagerSaveException;
import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {
    static File file;

    public FileBackedTasksManager(String stringPath) {
        file = new File(stringPath);
    }

    public FileBackedTasksManager() {
    }


  /*  public static void main(String[] args) throws ManagerSaveException {
        FileBackedTasksManager manager = (FileBackedTasksManager) Managers.getFileBacked();
        Epic epc1 = new Epic("epc1", "this is epic N1");
        Epic epc2 = new Epic("epc2", "this is epic N2");
        manager.addEpic(epc1);
        manager.addEpic(epc2);
        SubTask sbt1 = new SubTask("sbt1", "this is subtask1", epc1.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 10, 0), Duration.ofMinutes(60));

        SubTask sbt2 = new SubTask("sbt2", "this is subtask2", epc1.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 11, 0), Duration.ofMinutes(60));

        SubTask sbt3 = new SubTask("sbt3", "this is subtask3", epc2.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 12, 0), Duration.ofMinutes(60));

        SubTask sbt4 = new SubTask("sbt4", "this is subtask4", epc2.getTaskId(),
                LocalDateTime.of(2022, 4, 16, 13, 0), Duration.ofMinutes(60));


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
        task.setStartTime(LocalDateTime.of(2024, 2, 3, 12, 0));
        task.setStatus(TaskStatus.DONE);
        task.setDuration(Duration.ofMinutes(60));
        manager.addTask(task);
        manager.updateTask(task);
        manager.getTaskById(7);

        Task task2 = new Task("Taskname2", "Taskdescription2");
        task2.setStartTime(LocalDateTime.of(2055, 11, 3, 12, 0));
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setDuration(Duration.ofMinutes(120));
        manager.addTask(task2);
        manager.updateTask(task2);
        manager.getTaskById(8);

        FileBackedTasksManager newManager = loadFromFile(file);
        String firstManager = manager.getHistoryManager().getHistory().toString();
        String secondManager = newManager.getHistoryManager().getHistory().toString();
        System.out.println(firstManager.equals(secondManager));
        System.out.println(firstManager);
        System.out.println(secondManager);

        manager.addEpic(new Epic("newEpicName", "newEpicDescription"));
    }*/

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        List<String> listOfStrings;
        FileBackedTasksManager manager;
        try {
            listOfStrings = new ArrayList<>(Files.readAllLines(Paths.get(file.getName())));
            manager = new FileBackedTasksManager(file.getPath());

            for (int i = 1; i < listOfStrings.size() - 2; i++) {
                Task task = manager.fromString(listOfStrings.get(i));
                if (task instanceof Epic) {
                    manager.createEpicWhileLoading((Epic) task);
                }
            }
            for (int i = 1; i < listOfStrings.size() - 2; i++) {
                Task task = manager.fromString(listOfStrings.get(i));
                if (task instanceof SubTask) {
                    manager.createSubTaskWhileLoading((SubTask) task);
                } else if (task != null && !(task instanceof Epic)) {
                    manager.createTaskWhileLoading(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке");
        }
        //тут восстанавливаем историю
        if (listOfStrings.size() > 0) {
            String[] tempHistory = listOfStrings.get(listOfStrings.size() - 1).split(",");
            for (int i = 0; i < tempHistory.length; i++) {
                int historyElement = Integer.parseInt(tempHistory[i]);
                manager.getHistoryManager().add(manager.getAllTasksById(historyElement));
            }
        }
        manager.setId(manager.getEpicsMap().size() + manager.getTasksMap().size() + manager.getSubTasksMap().size());
        manager.save();
        return manager;
    }

    public void save() throws ManagerSaveException {
        try (PrintWriter pw = new PrintWriter(file.getName())) {
            pw.println("id,type,name,status,description,epic,startTime,duration,endTime");
            for (Task task : getAllTasksList()) {
                pw.println(task.toString());
            }
            for (SubTask task : getAllSubTasksList()) {
                pw.println(task.toString());
            }
            for (Epic task : getAllEpicsList()) {
                pw.println(task.toString());
            }
            pw.println("");

            List<Task> temp = history();
            if (temp != null && !temp.isEmpty()) {
                pw.print(temp.get(0).getTaskId());
                for (int i = 1; i < temp.size(); i++) {
                    pw.print("," + temp.get(i).getTaskId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении");
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    public void addSubTask(SubTask subtask, long epicId) {
        super.addSubTask(subtask, epicId);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(long id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void removeFromHistoryByID(long id) {
        super.removeFromHistoryByID(id);
        save();
    }

    @Override
    public Epic getEpicById(long taskId) {
        if (getEpicsMap().get(taskId) != null) {
            getHistoryManager().add(getEpicsMap().get(taskId));
            save();
            return getEpicsMap().get(taskId);
        } else
            return null;
    }

    @Override
    public Task getTaskById(long taskId) {
        if (getTasksMap().get(taskId) != null) {
            getHistoryManager().add(getTasksMap().get(taskId));
            save();
            return getTasksMap().get(taskId);
        } else
            return null;
    }

    @Override
    public Task getSubTaskById(long taskId) {
        if (getSubTasksMap().get(taskId) != null) {
            getHistoryManager().add(getSubTasksMap().get(taskId));
            save();
            return getSubTasksMap().get(taskId);
        } else
            return null;
    }


    Task fromString(String value) {
        String[] temp = value.split(",");

        switch (temp[1]) {
            case "SUBTASK":
                SubTask subTask = new SubTask(temp[2], temp[4], Integer.parseInt(temp[5]), LocalDateTime.parse(temp[6]), Duration.parse(temp[7]));
                subTask.setStatus(TaskStatus.valueOf(temp[3]));
                subTask.setTaskId(Integer.parseInt(temp[0]));
                return subTask;
            case "EPIC":
                Epic epic = new Epic((temp[2]), temp[4]);
                if (!temp[5].equals("null")) {
                    epic.setStartTime(null);
                }
                if (!temp[6].equals("null")) {
                    epic.setDuration(Duration.parse(temp[6]));
                }
                if (!temp[3].equals("null")) {
                    epic.setStatus(TaskStatus.valueOf(temp[3]));
                }
                if (!temp[7].equals("null")) {
                    epic.setEndTime(LocalDateTime.parse(temp[7]));
                }
                epic.setTaskId(Integer.parseInt(temp[0]));
                return epic;

            case "TASK":
                Task task = new Task(temp[2], temp[4], LocalDateTime.parse(temp[5]), Duration.parse(temp[6]));
                task.setStatus(TaskStatus.valueOf(temp[3]));
                task.setTaskId(Integer.parseInt(temp[0]));
                return task;

            default:
                return null;
        }
    }


}
