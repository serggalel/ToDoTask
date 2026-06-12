package com.internship.todotask.user.service;

import com.internship.todotask.user.exception.UserAlreadyExistsException;
import com.internship.todotask.user.model.dictionary.Role;
import com.internship.todotask.user.model.dto.UserCollabDto;
import com.internship.todotask.user.model.dto.UserDto;
import com.internship.todotask.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserOperationsService {

    String register(UserDto userDto) throws UserAlreadyExistsException;

    Page<UserEntity> getPaginatedUsers(int page, int size, String sortBy, String direction);

    Optional<UserEntity> getUserById(Long userId);

    String updateUser(Long userId, UserDto newUserDto, Role role);

    String deleteUser(Long userId);

    String removeCollaborator(Long userId, Long taskId);

    Page<UserCollabDto> getAllCollaborators(Long taskId, int page, int size, String sortBy, String direction);

    List<UserCollabDto> getPotentialCollaborators(Long taskId);

    String addCollaborator(Long userId, Long taskId);

}
