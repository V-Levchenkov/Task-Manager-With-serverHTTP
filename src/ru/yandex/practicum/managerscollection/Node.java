package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.tasks.Task;

import java.util.Objects;

public class Node {
    private Task task;
    private Node next;
    private Node prev;

    public Node(Node prev, Task task, Node next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    public Node() {
    }

    public Task getTask() {
        return task;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(task, node.task) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, next, prev);
    }

    @Override
    public String toString() {
        return "Node{" +
                "task= '" + task + '}';

    }
}