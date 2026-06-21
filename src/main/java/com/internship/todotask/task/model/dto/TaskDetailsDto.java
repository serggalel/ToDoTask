package com.internship.todotask.task.model.dto;

import com.internship.todotask.task.model.dictionary.Priority;
import com.internship.todotask.task.model.dictionary.PriorityConverter;
import com.internship.todotask.task.model.dictionary.State;
import com.internship.todotask.task.model.dictionary.StateConverter;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskDetailsDto {

    @NotNull(message = "Id can not be empty")
    @Positive
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Priority is mandatory")
    @Convert(converter = PriorityConverter.class)
    private Priority priority;

    @NotNull(message = "Priority is mandatory")
    @Convert(converter = StateConverter.class)
    private State state;

}
