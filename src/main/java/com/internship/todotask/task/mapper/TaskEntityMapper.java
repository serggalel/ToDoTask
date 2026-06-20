package com.internship.todotask.task.mapper;

import com.internship.todotask.task.model.dictionary.State;
import com.internship.todotask.task.model.dto.TaskDetailsDto;
import com.internship.todotask.task.model.dto.TaskDto;
import com.internship.todotask.task.model.entity.TaskEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityMapper {

    public TaskEntity toEntity(TaskDto taskDto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName(taskDto.getName());
        taskEntity.setPriority(taskDto.getPriority());
        taskEntity.setState(State.TODO);
        taskEntity.setOwnerId(taskDto.getOwnerId());
        return taskEntity;
    }

    public TaskDetailsDto fromEntity(TaskEntity taskEntity) {
        TaskDetailsDto taskDetailsDto = new TaskDetailsDto();
        taskDetailsDto.setId(taskEntity.getId());
        taskDetailsDto.setName(taskEntity.getName());
        taskDetailsDto.setPriority(taskEntity.getPriority());
        taskDetailsDto.setState(taskEntity.getState());
        return taskDetailsDto;
    }

}
