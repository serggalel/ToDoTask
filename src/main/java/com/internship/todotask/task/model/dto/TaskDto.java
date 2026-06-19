package com.internship.todotask.task.model.dto;

import com.internship.todotask.task.model.dictionary.Priority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Priority is mandatory")
    private Priority priority;

    @NotNull(message = "The task must have the owner")
    @Positive
    private Long ownerId;

}
