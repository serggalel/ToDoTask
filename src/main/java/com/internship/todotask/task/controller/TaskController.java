package com.internship.todotask.task.controller;

import com.internship.todotask.task.model.dto.TaskDetailsDto;
import com.internship.todotask.task.model.dto.TaskDto;
import com.internship.todotask.task.model.dto.UserTaskDto;
import com.internship.todotask.task.service.TaskService;
import com.internship.todotask.user.model.dto.UserDetailsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    @ResponseBody
    public String createTask(
            @RequestBody @Valid TaskDto taskDto,
            @AuthenticationPrincipal UserDetailsDto userDetailsDto) {
        return taskService.createTask(userDetailsDto.getId(), taskDto);
    }

    @PutMapping("/update")
    @ResponseBody
    public String updateTask(
            @RequestBody @Valid TaskDetailsDto taskDetailsDto,
            @AuthenticationPrincipal UserDetailsDto userDetailsDto
            ) {
        return taskService.updateTask(userDetailsDto.getId(), taskDetailsDto);
    }

    @GetMapping("/getByUser/{userId}")
    @ResponseBody
    public UserTaskDto getTasksByUser(
            @PathVariable Long userId,
            @Qualifier("owned") @PageableDefault(sort = "id") Pageable owned,
            @Qualifier("collab")@PageableDefault(sort = "id") Pageable collab
            ) {
        return taskService.getTasksByUserId(userId, owned, collab);
    }

    @DeleteMapping("/delete/{taskId}")
    @ResponseBody
    public String deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetailsDto userDetailsDto
    ) {
        return taskService.deleteTask(taskId, userDetailsDto.getId());
    }

}
