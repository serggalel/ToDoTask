package com.internship.todotask.user.repository;

import com.internship.todotask.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityById(Long id);

    Optional<UserEntity> findUserEntityByEmail(String email);

    void deleteUserEntityById(Long id);

    @Query(value = "SELECT user_id FROM user_task WHERE task_id = :taskId", nativeQuery = true)
    Set<Long> getUserIdsByTaskId(@Param("taskId") Long taskId);

    Page<UserEntity> findAllByIdIn(Set<Long> id, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM user_task WHERE user_id = :userId AND task_id = :taskId", nativeQuery = true)
    void deleteCollaborator(@Param("userId") Long userId, @Param("taskId") Long taskId);

}