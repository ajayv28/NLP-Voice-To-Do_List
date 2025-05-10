package com.ajay.nlp_voice_todo_list.service;

import com.ajay.nlp_voice_todo_list.common.StringConstants;
import com.ajay.nlp_voice_todo_list.entity.Task;
import com.ajay.nlp_voice_todo_list.repository.TaskRepository;
import com.ajay.nlp_voice_todo_list.util.MyAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private MyAnalyzer myAnalyzer = new MyAnalyzer(StringConstants.stopWordsList,StringConstants.protectedTermsList);

    public Task createTask(String task, String urgency, String datetime, Long userId) {

        Task newTask = Task.builder()
                .task(myAnalyzer.stem(task))
                .urgency(urgency)
                .datetime(datetime)
                .userId(userId)
                .build();

        return taskRepository.save(newTask);
    }


    public List<Task> getTask(Long userId) {
        List<Task> tasks =  taskRepository.findByUserId(userId);

        Map<String, Integer> urgencyPriority = Map.of(
                "High", 3,
                "Medium", 2,
                "Low", 1
        );

        tasks.sort((a, b) -> {
            int priorityA = urgencyPriority.getOrDefault(a.getUrgency(), 0);
            int priorityB = urgencyPriority.getOrDefault(b.getUrgency(), 0);
            return Integer.compare(priorityB, priorityA);
        });

        return tasks;
    }


    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }


    public Task updateTask(Long id, String task, String urgency, String datetime) {

        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isPresent()) {
            Task existingTask = taskOptional.get();

            existingTask.setTask(myAnalyzer.stem(task));
            existingTask.setUrgency(urgency);
            existingTask.setDatetime(datetime);

            return taskRepository.save(existingTask);
        }
        return null;
    }


    public boolean deleteTask(Long id) {

        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
