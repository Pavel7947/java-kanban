package service;

import model.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> browsingHistory;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        browsingHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Node node = browsingHistory.get(task.getId());
        removeNode(node);
        node = linkLast(task);
        browsingHistory.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        Node node = browsingHistory.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node node = first;

        while (node != null) {
            tasks.add(node.value);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            if (node == first && node == last) {
                first = null;
                last = null;
            } else if (node == first) {
                Node nextNode = node.next;
                nextNode.prev = null;
                first = nextNode;
            } else if (node == last) {
                Node prevNode = node.prev;
                prevNode.next = null;
                last = prevNode;
            } else {
                Node prevNode = node.prev;
                Node nextNode = node.next;

                prevNode.next = nextNode;
                nextNode.prev = prevNode;
            }
        }

    }

    private Node linkLast(Task task) {
        Node node = new Node(task);

        if (first == null) {
            first = node;
        } else {
            node.prev = last;
            last.next = node;
        }
        last = node;
        return node;
    }

    private static class Node {
        Node prev;
        Node next;
        Task value;

        public Node(Task value) {
            this.value = value;
        }
    }
}

