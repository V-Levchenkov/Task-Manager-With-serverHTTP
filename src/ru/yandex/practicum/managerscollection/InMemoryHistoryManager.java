package ru.yandex.practicum.managerscollection;

import ru.yandex.practicum.managerscollection.interfaces.HistoryManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    private Knot history = new Knot();
    private Map<Long, Node> nodeMap = new HashMap<>();

    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        } else {
            long idTask = task.getTaskId();
            if (nodeMap.containsKey(idTask)) {
                Node node = nodeMap.get(idTask);
                history.removeNode(node);
                history.linkLast(task);
            } else {
                history.linkLast(task);
            }
        }
    }

    @Override
    public void remove(Long id) {
        if (nodeMap.containsKey(id)) {
            Node node = nodeMap.get(id);
            Task task = node.getTask();
            Epic epic = new Epic();
            if (task.getClass() == epic.getClass()) {
                epic = (Epic) task;
                for (Long idSubTask : epic.getSubTaskId()) {
                    Node nodeSubTask = nodeMap.get(idSubTask);
                    history.removeNode(nodeSubTask);
                }
            }
            history.removeNode(node);
        } else {
            System.out.println("Нет такой задачи в истории");
        }
    }

    public class Knot {

        public Node head;
        public Node tail;

        public void linkLast(Task task) {
            final Node oldLastNode = tail;
            final Node newNode = new Node(oldLastNode, task, null);
            tail = newNode;
            if (oldLastNode == null)
                head = newNode;
            else
                oldLastNode.setNext(newNode);
            nodeMap.put(task.getTaskId(), newNode);
        }

        public void removeNode(Node node) {
            if (head == null && tail == null) {
                return;
            } else if (head.getNext() == null && tail.getPrev() == null) {
                head = tail = null;
            } else {
                if (node.getPrev() == null) {
                    head = head.getNext();
                    head.setPrev(null);
                } else if (node.getNext() == null) {
                    tail = tail.getPrev();
                    tail.setNext(null);
                } else {
                    Node prevTaskNode = node.getPrev();
                    Node nextTaskNode = node.getNext();
                    prevTaskNode.setNext(nextTaskNode);
                    nextTaskNode.setPrev(prevTaskNode);
                }
            }
            nodeMap.remove(node.getTask().getTaskId());
        }

        public List<Task> getTasks() {
            List<Task> taskHistory = new ArrayList<>();
            Node node = head;
            while (node != null) {
                taskHistory.add(node.getTask());
                node = node.getNext();
            }
            return taskHistory;
        }
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Task task : getHistory()) {
            if (result.length() == 0) {
                result.append(task.getTaskId());
            }else{
                result.append(",");
                result.append(task.getTaskId());
            }
        }
        return result.toString();
    }
}

