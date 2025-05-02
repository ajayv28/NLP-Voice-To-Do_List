package com.ajay.nlp_voice_todo_list.service;

import com.ajay.nlp_voice_todo_list.model.TaskRequest;
import com.ajay.nlp_voice_todo_list.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private EmailService emailService;

    public void sendTaskNotifications(TaskRequest taskRequest, User user, boolean isUpdate) {

        if (user != null) {

            String action = isUpdate ? "updated" : "created";
            String emailMessage = String.format("Task %s: %s\nUrgency: %s\nDue: %s",
                    action, taskRequest.getTask(), taskRequest.getUrgency(), taskRequest.getDatetime());

            emailService.sendTaskNotification(
                    user.getEmail(),
                    "Task " + action.substring(0, 1).toUpperCase() + action.substring(1),
                    emailMessage
            );
        }
    }

}
