package com.internship.todotask.task.service;

import com.internship.todotask.task.model.dto.TaskDetailsDto;
import com.internship.todotask.task.model.dto.TaskDto;
import com.internship.todotask.task.model.dto.UserTaskDto;
import com.internship.todotask.task.model.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    String createTask(Long userId, TaskDto taskDto);

    String updateTask(Long userId, TaskDetailsDto taskDetailsDto);

    UserTaskDto getTasksByUserId(Long userId, Pageable ownedPageable, Pageable collabPageable);

    String deleteTask(Long taskId, Long userId);

}
