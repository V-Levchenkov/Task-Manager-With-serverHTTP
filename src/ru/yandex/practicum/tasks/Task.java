package ru.yandex.practicum.tasks;

import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.managerscollection.interfaces.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String taskName;
    private String taskDescription;
    private long taskId;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task() {
    }

    public Task(final String TaskName, final String TaskDescription) {
        this.taskName = TaskName;
        this.taskDescription = TaskDescription;
    }

    public Task(String TaskName, String TaskDescription, LocalDateTime startTime, Duration duration) {
        this.taskName = TaskName;
        this.taskDescription = TaskDescription;
        this.startTime = startTime;
        this.duration = duration;
        setStatus(TaskStatus.NEW);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public long taskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    TypeTask getType() {
        return TypeTask.TASK;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;

    }

    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return getTaskId() + "," + getType() + "," + getTaskName() + "," + getStatus() + "," + getTaskDescription() +
                "," + getStartTime() + "," + getDuration() + "," + getEndTime();
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setDuration(final Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(final LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        final Task task = (Task) o;
        return getTaskId() == task.getTaskId() && Objects.equals(getTaskName(), task.getTaskName()) && Objects.equals(getTaskDescription(), task.getTaskDescription()) && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskName(), getTaskDescription(), getTaskId(), getStatus());
    }

}