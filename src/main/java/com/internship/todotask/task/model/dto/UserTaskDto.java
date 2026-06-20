package com.internship.todotask.task.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskDto {

    private Page<TaskDetailsDto> ownedTasks;

    private Page<TaskDetailsDto> collabTasks;

}
