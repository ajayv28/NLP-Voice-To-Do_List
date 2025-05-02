package com.ajay.nlp_voice_todo_list.repository;

import com.ajay.nlp_voice_todo_list.entity.Task;
import com.ajay.nlp_voice_todo_list.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);
}
