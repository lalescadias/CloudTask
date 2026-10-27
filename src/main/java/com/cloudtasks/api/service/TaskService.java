package com.cloudtasks.api.service;

import com.cloudtasks.api.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task createTask(Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setCompleted(false);

        tasks.add(task);
        return task;
    }

    public Task getTaskById(String id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found"
                        ));
    }

    public void deleteTask(String id) {
        Task task = getTaskById(id);
        tasks.remove(task);
    }
    public Task updateTask(String id, Task updatedTask) {
        Task task = getTaskById(id);

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.isCompleted());

        return task;
    }
}