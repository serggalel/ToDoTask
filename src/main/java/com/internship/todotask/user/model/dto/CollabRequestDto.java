package com.internship.todotask.user.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
public class CollabRequestDto {

    @NotNull(message = "UserId is mandatory")
    @Positive
    private Long userId;

    @NotNull(message = "TaskId is mandatory")
    @Positive
    private Long taskId;

}
