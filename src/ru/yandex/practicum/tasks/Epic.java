package ru.yandex.practicum.tasks;

import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.managerscollection.interfaces.TypeTask;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static ru.yandex.praktikum.utils.CSVutil.splitter;

public class Epic extends Task {
    private LocalDateTime epicEndTime;
    private ArrayList<Long> subTaskId = new ArrayList<>();


    public Epic(String taskName, String taskDescription, TaskStatus status, Long epicId, int duration, LocalDateTime startTime, LocalDateTime epicEndTime) {
        super(taskName, taskDescription, status, epicId, duration, startTime);
        this.epicEndTime = epicEndTime;
    }

    public Epic(String taskName, String taskDescription, TaskStatus status, Long epicId) {
        super(taskName, taskDescription, status, epicId);
    }
    public Epic(String taskName, String taskDescription, TaskStatus status) {
        super(taskName, taskDescription, status);
    }

    public Epic(String nameTask, String description) {
        super(nameTask, description);
    }

    public Epic() {
    }

    public void setIdSubTasks(Long subTaskId) {
        this.subTaskId.add(subTaskId);
    }
    public LocalDateTime getEpicEndTime() {
        return epicEndTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return epicEndTime;
    }

    public void setEpicEndTime(LocalDateTime epicEndTime) {
        this.epicEndTime = epicEndTime;
    }

    public ArrayList<Long> getSubTaskId() {
        return subTaskId;
    }

    public void addSubTaskId(long subTaskId) {
        this.subTaskId.add(subTaskId);
    }

    @Override
    public String toString() {
        String result;
        try {
            String q = String.valueOf(getStartTime());
            String w = String.valueOf(getEndTime());
            result = getTaskId().toString() + splitter + TypeTask.Epic + splitter + getTaskName() +
                    splitter + getStatus() + splitter + getTaskDescription() + splitter +
                    q + splitter + getDuration() + splitter + w;
        } catch (NullPointerException ex) {
            String q = String.valueOf(getStartTime());
            String w = "null";
            result = getTaskId().toString() + splitter + TypeTask.Epic + splitter + getTaskName() +
                    splitter + getStatus() + splitter + getTaskDescription() + splitter +
                    q + splitter + getDuration() + splitter + w;
        }

        return result;

    }
}
