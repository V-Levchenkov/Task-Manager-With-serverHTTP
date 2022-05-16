package ru.yandex.practicum.tasks;

import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.managerscollection.interfaces.TypeTask;

import java.time.LocalDateTime;

import static ru.yandex.praktikum.utils.CSVutil.splitter;

public class Subtask extends Task {
    private Long epicId;


    public Subtask(String taskName, String taskDescription, TaskStatus status, Long taskId, int duration, LocalDateTime startTime, Long epicId) {
        super(taskName, taskDescription, status, taskId, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, int duration, LocalDateTime startTime, Long epicId) {
        super(taskName, taskDescription, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, Long epicId) {
        super(taskName, taskDescription);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, TaskStatus status, Long epicId) {
        super(taskName, taskDescription, status);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, TaskStatus status, Long taskId, Long epicId) {
        super(taskName, taskDescription, status, taskId);
        this.epicId = epicId;
    }

    public Subtask() {
    }


    public long getEpicId() {
        if (epicId != null) {
            return epicId;
        } else {
            throw new NullPointerException("epicId = null");
        }

    }

    public void setEpicId(Long epicId) {
        if (epicId != null) {
            if (epicId > 0) {
                this.epicId = epicId;
            } else {
                throw new IllegalArgumentException("epicId имеет отрицательное значение");
            }
        } else {
            throw new NullPointerException("переданный epicId = null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId.equals(subtask.epicId);
    }

    @Override
    public String toString() {
        String result;
        try {
            String q = String.valueOf(getStartTime());
            String w = String.valueOf(getEndTime());
            result = getTaskId().toString() + splitter + TypeTask.Subtask + splitter + getTaskName() +
                    splitter + getStatus() + splitter + getTaskDescription() + splitter +
                    q + splitter + getDuration() + splitter + w;
        } catch (NullPointerException ex) {
            String q = String.valueOf(getStartTime());
            String w = "null";
            result = getTaskId().toString() + splitter + TypeTask.Subtask + splitter + getTaskName() +
                    splitter + getStatus() + splitter + getTaskDescription() + splitter +
                    q + splitter + getDuration() + splitter + w;
        }

        return result;

    }
}