package ru.yandex.practicum.tasks;

import ru.yandex.practicum.managerscollection.interfaces.TaskStatus;
import ru.yandex.practicum.managerscollection.interfaces.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private long epicId;

    public SubTask(String name, String description, long epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        setStatus(TaskStatus.NEW);
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getTaskId() + "," + getType() + "," + getTaskName() + "," + getStatus() + "," + getTaskDescription() + ","
                + getEpicId() + "," + getStartTime() + "," + getDuration() + "," + getEndTime();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        final SubTask subTask = (SubTask) o;
        return getEpicId() == (subTask.getEpicId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }
}
