package service;

import model.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> browsingHistory;
    private Node<Task> first;
    private Node<Task> last;

    public InMemoryHistoryManager() {
        browsingHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Node<Task> node = browsingHistory.get(task.getId());
        removeNode(node);
        node = linkLast(task);
        browsingHistory.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = browsingHistory.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = first;

        while (node != null) {
            tasks.add(node.value);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            if (node == first && node == last) {
                first = null;
                last = null;
            } else if (node == first) {
                Node<Task> nextNode = node.next;
                nextNode.prev = null;
                first = nextNode;
            } else if (node == last) {
                Node<Task> prevNode = node.prev;
                prevNode.next = null;
                last = prevNode;
            } else {
                Node<Task> prevNode = node.prev;
                Node<Task> nextNode = node.next;

                prevNode.next = nextNode;
                nextNode.prev = prevNode;
            }
        }

    }

    private Node<Task> linkLast(Task task) {
        Node<Task> node = new Node<>(task);

        if (first == null) {
            first = node;
        } else {
            node.prev = last;
            last.next = node;
        }
        last = node;
        return node;
    }

    private static class Node<E> {
        Node<E> prev;
        Node<E> next;
        E value;

        public Node(E value) {
            this.value = value;
        }
    }
}

