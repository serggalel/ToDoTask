package com.internship.todotask.user.service.impl;

import com.internship.todotask.task.exception.TaskNotFoundException;
import com.internship.todotask.task.exception.UserCannotBeCollaboratorException;
import com.internship.todotask.task.model.entity.TaskEntity;
import com.internship.todotask.task.repository.TaskRepository;
import com.internship.todotask.user.exception.UserAlreadyExistsException;
import com.internship.todotask.user.exception.UserNotFoundException;
import com.internship.todotask.user.mapper.UserCollabDtoMapper;
import com.internship.todotask.user.mapper.UserDtoMapper;
import com.internship.todotask.user.model.dto.CollabRequestDto;
import com.internship.todotask.user.model.dto.UserCollabDto;
import com.internship.todotask.user.model.dto.UserDetailsDto;
import com.internship.todotask.user.model.dto.UserDto;
import com.internship.todotask.user.model.entity.UserEntity;
import com.internship.todotask.user.repository.UserRepository;
import com.internship.todotask.user.service.UserOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.internship.todotask.task.service.impl.TaskServiceImpl.TASK_NOT_FOUND_STRING;

@Service
@RequiredArgsConstructor
public class UserOperationsServiceImpl implements UserOperationsService {

    private final UserRepository userRepository;

    private final UserDtoMapper userDtoMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserCollabDtoMapper userCollabDtoMapper;

    private final TaskRepository taskRepository;

    private static final String USER_NOT_FOUND_STRING = "User was not found with id: ";

    @Override
    public String register(UserDto userDto) throws UserAlreadyExistsException {
        String email = userDto.getEmail();
        if (emailExists(email)) throw new UserAlreadyExistsException("User already exists with email: " + email);
        userRepository.save(userDtoMapper.toEntity(userDto));
        return "Successfully registered!";
    }

    @Override
    public Page<UserEntity> getPaginatedUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<UserEntity> getUserById(Long userId) {
        if (userIdDoesntExist(userId)) throw new UserNotFoundException(USER_NOT_FOUND_STRING + userId);
        return userRepository.findUserEntityById(userId);
    }

    @Transactional
    @Override
    public String updateUser(UserDetailsDto newUserDto) {
        Long userId = newUserDto.getId();
        UserEntity existingUser = userRepository.findUserEntityById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_STRING + userId));
        existingUser.setFirstName(newUserDto.getFirstName());
        existingUser.setLastName(newUserDto.getLastName());
        existingUser.setEmail(newUserDto.getEmail());
        existingUser.setPassword(passwordEncoder.encode(newUserDto.getPassword()));
        existingUser.setRole(newUserDto.getRole());
        userRepository.save(existingUser);
        return "The update was successful!";
    }

    @Transactional
    @Override
    public String deleteUser(Long userId) {
        if (userIdDoesntExist(userId)) throw new UserNotFoundException(USER_NOT_FOUND_STRING + userId);

        List<TaskEntity> ownedTasks = taskRepository.findTaskEntitiesByOwnerId(userId);
        List<TaskEntity> tasksToDelete = new ArrayList<>();
        List<TaskEntity> tasksToUpdate = new ArrayList<>();
        if (!ownedTasks.isEmpty()) {
            for (TaskEntity task : ownedTasks) {
                List<UserEntity> collaborators = userRepository.findAllCollaborators(task.getId());
                if (collaborators.isEmpty()) {
                    tasksToDelete.add(task);
                } else {
                    Long newOwnerId = collaborators.get(0).getId();
                    task.setOwnerId(newOwnerId);
                    tasksToUpdate.add(task);
                    userRepository.deleteCollaborator(newOwnerId, task.getId());
                }
            }
            if (!tasksToUpdate.isEmpty()) {
                taskRepository.saveAllAndFlush(tasksToUpdate);
            }
            if (!tasksToDelete.isEmpty()) {
                taskRepository.deleteCollabsByTaskIds(tasksToDelete.stream().map(TaskEntity::getId).toList());
                taskRepository.deleteAllInBatch(tasksToDelete);
            }
        }
        userRepository.deleteCollabOnUserDeletion(userId);
        userRepository.deleteUserEntityById(userId);
        return "The deletion was successful!";
    }

    @Transactional
    @Override
    public String removeCollaborator(CollabRequestDto collabRequestDto) {
        Long userId = collabRequestDto.getUserId();
        if (userIdDoesntExist(userId)) throw new UserNotFoundException(USER_NOT_FOUND_STRING + userId);

        Long taskId = collabRequestDto.getTaskId();
        if (taskIdDoesntExist(taskId))
            throw new TaskNotFoundException(TASK_NOT_FOUND_STRING + taskId);

        userRepository.deleteCollaborator(collabRequestDto.getUserId(), collabRequestDto.getTaskId());
        return "The collaborator was successfully removed!";
    }

    @Override
    public Page<UserCollabDto> getAllCollaborators(Long taskId, Pageable pageable) {
        return userRepository.findAllCollaborators(taskId, pageable).map(userCollabDtoMapper::fromEntity);
    }

    @Override
    public List<UserCollabDto> getPotentialCollaborators(Long taskId) {
        return userRepository.findPotentialCollaborators(taskId)
                .stream()
                .filter(user -> !userIsTheOwner(user.getId(), taskId))
                .map(userCollabDtoMapper::fromEntity)
                .toList();
    }

    @Transactional
    @Override
    public String addCollaborator(CollabRequestDto collabRequestDto) {
        Long userId = collabRequestDto.getUserId();
        if (userIdDoesntExist(userId)) throw new UserNotFoundException(USER_NOT_FOUND_STRING + userId);

        Long taskId = collabRequestDto.getTaskId();
        if (taskIdDoesntExist(taskId))
            throw new TaskNotFoundException(TASK_NOT_FOUND_STRING + taskId);

        if (userIsTheOwner(userId, taskId)) throw new UserCannotBeCollaboratorException("User is the owner of the task so cannot be the collaborator!");
        if (userIsAlreadyCollaborator(userId, taskId)) throw new UserCannotBeCollaboratorException("User is already a collaborator of the task!");

        userRepository.addCollaborator(collabRequestDto.getUserId(), collabRequestDto.getTaskId());
        return "Collaborator added successfully!";
    }

    private boolean emailExists(String email) {
        return userRepository.findUserEntityByEmail(email).isPresent();
    }

    private boolean userIdDoesntExist(Long userId) {
        return userRepository.findUserEntityById(userId).isEmpty();
    }

    private boolean taskIdDoesntExist(Long taskId) {
        return taskRepository.findTaskEntityById(taskId).isEmpty();
    }

    private boolean userIsTheOwner(Long userId, Long taskId) {
        return taskRepository.findTaskEntityById(taskId)
                .map(TaskEntity::getOwnerId)
                .map(ownerId -> ownerId.equals(userId))
                .orElse(false);
    }

    private boolean userIsAlreadyCollaborator(Long userId, Long taskId) {
        return userRepository.findAllCollaborators(taskId)
                .stream()
                .anyMatch(uId -> uId.getId().equals(userId));
    }

}
