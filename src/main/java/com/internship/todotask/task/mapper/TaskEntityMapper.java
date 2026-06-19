package com.internship.todotask.task.mapper;

import com.internship.todotask.task.model.dto.TaskDto;
import com.internship.todotask.task.model.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityMapper {

    public TaskEntity toEntity(TaskDto taskDto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName(taskDto.getName());
        taskEntity.setPriority(taskDto.getPriority());
        taskEntity.setOwnerId(taskDto.getOwnerId());
        return taskEntity;
    }

}
