package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subTaskIdList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration,
                LocalDateTime endTime) {
        super(name, description, status, startTime, duration);
        this.endTime = endTime;
        subTaskIdList = new ArrayList<>();
    }

    public List<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (endTime != null) {
            return endTime;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "endTime=" + endTime +
                ", subTaskIdList=" + subTaskIdList +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIdList, epic.subTaskIdList) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIdList, endTime);
    }
}

