package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIdList;


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subTaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "subTaskIdList=" + subTaskIdList +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}

