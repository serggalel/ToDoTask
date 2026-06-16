package com.internship.todotask.user.controller;

import com.internship.todotask.user.model.dto.CollabRequestDto;
import com.internship.todotask.user.model.dto.UserCollabDto;
import com.internship.todotask.user.model.dto.UserDetailsDto;
import com.internship.todotask.user.model.dto.UserDto;
import com.internship.todotask.user.model.entity.UserEntity;
import com.internship.todotask.user.service.UserOperationsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserOperationsService userOperationsService;

    @PostMapping("/register")
    @ResponseBody
    public String registerUser(@Valid @RequestBody UserDto userDto) {
        return userOperationsService.register(userDto);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<Page<UserEntity>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<UserEntity> pageableEntities = userOperationsService.getPaginatedUsers(page, size, sortBy, direction);
        return ResponseEntity.ok(pageableEntities);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Optional<UserEntity> getUser(@PathVariable Long id) {
        return userOperationsService.getUserById(id);
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateUser(
            @Valid @RequestBody UserDetailsDto userDto
            ) {
        return userOperationsService.updateUser(userDto);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id) {
        return userOperationsService.deleteUser(id);
    }

    @GetMapping("/getCollabs/{taskId}")
    @ResponseBody
    public ResponseEntity<Page<UserCollabDto>> getAllCollaborators(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<UserCollabDto> userCollabDtoPage = userOperationsService.getAllCollaborators(taskId, page, size, sortBy, direction);
        return ResponseEntity.ok(userCollabDtoPage);
    }

    @GetMapping("/getPossCollabs/{taskId}")
    @ResponseBody
    public List<UserCollabDto> getPossibleCollaborators(@PathVariable Long taskId) {
        return userOperationsService.getPotentialCollaborators(taskId);
    }

    @DeleteMapping("/rmvCollab")
    @ResponseBody
    public String removeCollaborator(
            @Valid @RequestBody CollabRequestDto collabRequestDto
    ) {
        return userOperationsService.removeCollaborator(collabRequestDto);
    }

    @PostMapping("/addCollab")
    @ResponseBody
    public String addCollaborator(
            @Valid @RequestBody CollabRequestDto collabRequestDto
    ) {
        return userOperationsService.addCollaborator(collabRequestDto);
    }

}
