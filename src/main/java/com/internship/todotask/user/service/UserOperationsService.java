package com.internship.todotask.user.service;

import com.internship.todotask.user.exception.UserAlreadyExistsException;
import com.internship.todotask.user.model.dto.CollabRequestDto;
import com.internship.todotask.user.model.dto.UserCollabDto;
import com.internship.todotask.user.model.dto.UserDetailsDto;
import com.internship.todotask.user.model.dto.UserDto;
import com.internship.todotask.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserOperationsService {

    String register(UserDto userDto) throws UserAlreadyExistsException;

    Page<UserEntity> getPaginatedUsers(Pageable pageable);

    Optional<UserEntity> getUserById(Long userId);

    String updateUser(UserDetailsDto newUserDto);

    String deleteUser(Long userId);

    String removeCollaborator(CollabRequestDto collabRequestDto);

    Page<UserCollabDto> getAllCollaborators(Long taskId, Pageable pageable);

    List<UserCollabDto> getPotentialCollaborators(Long taskId);

    String addCollaborator(CollabRequestDto collabRequestDto);

}
