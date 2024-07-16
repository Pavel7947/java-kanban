package model;

import java.util.Objects;


public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    public Integer getEpicId() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    public Status getStatus() {
        return status;
    }


    @Override
    public String toString() {
        return "Tasks.Task{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}





