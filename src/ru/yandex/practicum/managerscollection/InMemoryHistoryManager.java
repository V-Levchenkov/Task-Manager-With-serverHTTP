package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.managerscollection.interfaces.HistoryManager;
import ru.yandex.practicum.tasks.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    public Node<Task> head;
    public Node<Task> tail;
    private int size = 0;
    private Map<Long, Node<Task>> map;

    public InMemoryHistoryManager() {
        map = new HashMap<Long, Node<Task>>();
    }

    public void linkLast(Task element) {
        Node<Task> t = tail;
        Node<Task> newNode = new Node<>(element, null, t);
        tail = newNode;
        if (t == null)
            head = newNode;
        else
            t.next = newNode;
        size++;
    }

    void removeNode(Node<Task> node) {
        if (node.prev == null && node.next == null) {
            head = null;
            tail = null;
            return;
        }
        if (node.prev == null) {
            head = node.next;
            node.next.prev = null;
        } else if (node.next == null) {
            tail = node.prev;
            node.prev.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        size--;
    }

    Node<Task> getLast(Node<Task> head) {
        Node<Task> temp = head;
        if (temp == null) {
            return null;
        }
        while (temp.next != null) {
            temp = temp.next;
        }
        return temp;
    }

    @Override
    public void add(Task task) {
        if (map.containsKey(task.getTaskId())) {
            removeNode(map.get(task.getTaskId()));
            remove(task.getTaskId());
        }
        linkLast(task);
        map.put(task.getTaskId(), getLast(head));
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> thisNode = this.head;
        if (thisNode != null) {
            while (thisNode.next != null) {
                tasks.add(thisNode.data);
                thisNode = thisNode.next;
            }
            tasks.add(thisNode.data);
        }
        return tasks;
    }

    @Override
    public int getSize() {
            return size;
        }


    @Override
    public void remove(long id) {
        if (map.containsKey(id)) {
            removeNode(map.get(id));
            map.remove(id);
        }
    }
}


