package com.internship.todotask.task.model.dto;

import com.internship.todotask.task.model.dictionary.Priority;
import com.internship.todotask.task.model.dictionary.PriorityConverter;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Convert(converter = PriorityConverter.class)
    @NotNull(message = "Priority is mandatory")
    private Priority priority;

    private Long ownerId;

}
