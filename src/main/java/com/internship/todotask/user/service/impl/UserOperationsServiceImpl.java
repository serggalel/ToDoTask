package com.internship.todotask.user.service.impl;

import com.internship.todotask.user.exception.UserAlreadyExistsException;
import com.internship.todotask.user.exception.UserNotFoundException;
import com.internship.todotask.user.mapper.UserCollabDtoMapper;
import com.internship.todotask.user.mapper.UserDtoMapper;
import com.internship.todotask.user.model.dictionary.Role;
import com.internship.todotask.user.model.dto.UserCollabDto;
import com.internship.todotask.user.model.dto.UserDto;
import com.internship.todotask.user.model.entity.UserEntity;
import com.internship.todotask.user.repository.UserRepository;
import com.internship.todotask.user.service.UserOperationsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserOperationsServiceImpl implements UserOperationsService {

    private final UserRepository userRepository;

    private final UserDtoMapper userDtoMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserCollabDtoMapper userCollabDtoMapper;

    private static final String USER_NOT_FOUND_STRING = "User was not found with id: ";

    @Override
    public String register(UserDto userDto) throws UserAlreadyExistsException {
        String email = userDto.getEmail();
        if (emailExists(email)) throw new UserAlreadyExistsException("User already exists with email: " + email);
        userRepository.save(userDtoMapper.toEntity(userDto));
        return "Successfully registered!";
    }

    @Override
    public Page<UserEntity> getPaginatedUsers(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<UserEntity> getUserById(Long userId) {
        if (userIdDoesntExist(userId)) throw new UserNotFoundException(USER_NOT_FOUND_STRING + userId);
        return userRepository.findUserEntityById(userId);
    }

    @Override
    public String updateUser(Long userId, UserDto newUserDto, Role role) {
        UserEntity existingUser = userRepository.findUserEntityById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_STRING + userId));
        existingUser.setFirstName(newUserDto.getFirstName());
        existingUser.setLastName(newUserDto.getLastName());
        existingUser.setEmail(newUserDto.getEmail());
        existingUser.setPassword(passwordEncoder.encode(newUserDto.getPassword()));
        existingUser.setRole(role);
        userRepository.save(existingUser);
        return "The update was successful!";
    }

    @Override
    public String deleteUser(Long userId) {
        if (userIdDoesntExist(userId)) throw new UserNotFoundException(USER_NOT_FOUND_STRING + userId);
        userRepository.deleteUserEntityById(userId);
        return "The deletion was successful!";
    }

    @Override
    public String removeCollaborator(Long userId, Long taskId) {
        userRepository.deleteCollaborator(userId, taskId);
        return "The collaborator was successfully removed!";
    }

    @Override
    public Page<UserCollabDto> getAllCollaborators(Long taskId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAllCollaborators(taskId, pageable).map(userCollabDtoMapper::fromEntity);
    }

    @Override
    public List<UserCollabDto> getPotentialCollaborators(Long taskId) {
        return userRepository.findPotentialCollaborators(taskId)
                .stream()
                .map(userCollabDtoMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public String addCollaborator(Long userId, Long taskId) {
        userRepository.addCollaborator(userId, taskId);
        return "Collaborator added successfully!";
    }

    private boolean emailExists(String email) {
        return userRepository.findUserEntityByEmail(email).isPresent();
    }

    private boolean userIdDoesntExist(Long userId) {
        return userRepository.findUserEntityById(userId).isEmpty();
    }

}
