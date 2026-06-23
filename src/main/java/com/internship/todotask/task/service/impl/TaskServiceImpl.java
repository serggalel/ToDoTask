package com.internship.todotask.task.service.impl;

import com.internship.todotask.task.exception.TaskNotFoundException;
import com.internship.todotask.task.exception.UserIsNotTheOwnerException;
import com.internship.todotask.task.mapper.TaskEntityMapper;
import com.internship.todotask.task.model.dto.TaskDetailsDto;
import com.internship.todotask.task.model.dto.TaskDto;
import com.internship.todotask.task.model.dto.UserTaskDto;
import com.internship.todotask.task.model.entity.TaskEntity;
import com.internship.todotask.task.repository.TaskRepository;
import com.internship.todotask.task.service.TaskService;
import com.internship.todotask.user.model.entity.UserEntity;
import com.internship.todotask.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final TaskEntityMapper taskEntityMapper;

    private final UserRepository userRepository;

    public static final String TASK_NOT_FOUND_STRING = "Task was not found with id: ";

    private static final String USER_IS_NOT_THE_OWNER_STRING = "User is not the owner of the task!";

    @Override
    public String createTask(Long userId, TaskDto taskDto) {
        TaskEntity taskEntity = taskEntityMapper.toEntity(taskDto);
        taskEntity.setOwnerId(userId);
        taskRepository.save(taskEntity);
        return "Successfully created a task!";
    }

    @Transactional
    @Override
    public String updateTask(Long userId, TaskDetailsDto taskDetailsDto) {
        Long taskId = taskDetailsDto.getId();
        TaskEntity taskEntity = taskRepository.findTaskEntityById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_STRING + taskId));
        if(!taskEntity.getOwnerId().equals(userId)) throw new UserIsNotTheOwnerException(USER_IS_NOT_THE_OWNER_STRING);
        taskEntity.setName(taskDetailsDto.getName());
        taskEntity.setPriority(taskDetailsDto.getPriority());
        taskEntity.setState(taskDetailsDto.getState());
        taskRepository.save(taskEntity);
        return "Task updated successfully!";
    }

    @Override
    public UserTaskDto getTasksByUserId(Long userId, Pageable ownedPageable, Pageable collabPageable) {

        UserEntity currentUser = userRepository.findById(userId).orElse(null);

        Page<TaskEntity> ownedEntities = taskRepository.findTaskEntitiesByOwnerId(userId, ownedPageable);
        Page<TaskEntity> collabEntities = taskRepository.findTaskEntitiesByCollaboratorId(userId, collabPageable);

        Page<TaskDetailsDto> ownedDtoPage = ownedEntities.map(task -> {
            TaskDetailsDto dto = taskEntityMapper.fromEntity(task);
            if (currentUser != null) {
                dto.setOwnerFirstName(currentUser.getFirstName());
                dto.setOwnerLastName(currentUser.getLastName());
            }
            return dto;
        });

        Page<TaskDetailsDto> collabDtoPage = collabEntities.map(task -> {
            TaskDetailsDto dto = taskEntityMapper.fromEntity(task);

            userRepository.findById(task.getOwnerId()).ifPresent(owner -> {
                dto.setOwnerFirstName(owner.getFirstName());
                dto.setOwnerLastName(owner.getLastName());
            });
            return dto;
        });
        return new UserTaskDto(ownedDtoPage, collabDtoPage);
    }

    @Transactional
    @Override
    public String deleteTask(Long taskId, Long userId) {
        TaskEntity taskEntity = taskRepository.findTaskEntityById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_STRING + taskId));
        if(!taskEntity.getOwnerId().equals(userId)) throw new UserIsNotTheOwnerException(USER_IS_NOT_THE_OWNER_STRING);
        taskRepository.deleteCollabOnTaskDeletion(taskId);
        taskRepository.delete(taskEntity);
        return "The deletion was successful!";
    }

}
