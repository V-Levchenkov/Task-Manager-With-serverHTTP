package ru.yandex.practicum.tasks;

import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.managerscollection.interfaces.TypeTask;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.yandex.praktikum.utils.CSVutil.splitter;

public class Task implements Comparable<Task> {
    protected String taskName;


    protected TypeTask typeTask;
    protected String taskDescription;
    protected Long taskId;
    private int duration;
    private LocalDateTime startTime;

    protected TaskStatus status;


    public Task(String taskName, String taskDescription, TaskStatus status, Long taskId, int duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.taskId = taskId;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String taskName, String taskDescription, int duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.duration = duration;
        this.startTime = startTime;
        status = TaskStatus.NEW;
    }

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        status = TaskStatus.NEW;
    }

    public Task(String taskName, String taskDescription, TaskStatus status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public Task(String taskName, String taskDescription, TaskStatus status, Long taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.taskId = taskId;
    }

    public Task(Long taskId, String name, String taskDescription, TaskStatus status, String startTime, String duration) {
        this.status = TaskStatus.NEW;
    }

    public Task() {
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TypeTask getTypeTask() {
        return typeTask;
    }

    public String getTaskName() {
        return taskName;
    }

    public Long getTaskId() {
        return this.taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId) && status == task.status && taskName.equals(task.taskName)
                && taskDescription.equals(task.taskDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, taskName, taskDescription, taskId);
    }

    @Override
    public String toString() {
        String result;
        try {
            String q = String.valueOf(startTime);
            String w = String.valueOf(getEndTime());
            result = taskId.toString() + splitter + TypeTask.Task + splitter + taskName + splitter
                    + status + splitter + taskDescription + splitter + q + splitter + duration + splitter + w;
        } catch (NullPointerException ex) {
            String q = String.valueOf(startTime);
            String w = "null";
            result = taskId.toString() + splitter + TypeTask.Task + splitter + taskName + splitter
                    + status + splitter + taskDescription + splitter + q + splitter + duration + splitter + w;
        }
        return result;
    }


    @Override
    public int compareTo(Task o) {
        if (this.startTime == null) {
            return 1;
        }
        if (o.startTime == null) {
            return -1;
        }
        return (this.startTime.compareTo(o.startTime));
    }
}

