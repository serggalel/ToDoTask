package com.internship.todotask.user.repository;

import com.internship.todotask.user.model.dto.UserCollabDto;
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

    @Modifying
    @Query(value = "DELETE FROM user_task WHERE user_id = :userId AND task_id = :taskId", nativeQuery = true)
    void deleteCollaborator(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_task ut ON u.id = ut.user_id " +
            "WHERE ut.task_id = :taskId", nativeQuery = true)
    Page<UserEntity> findAllCollaborators(@Param("taskId") Long taskId, Pageable pageable);

    @Query(value = "SELECT * FROM users " +
            "WHERE id NOT IN " +
            "(SELECT user_id FROM user_task WHERE task_id = :taskId)", nativeQuery = true)
    List<UserEntity> findPotentialCollaborators(Long taskId);

    @Modifying
    @Query(value = "INSERT INTO user_task " +
            "VALUES (:userId, :taskId)", nativeQuery = true)
    void addCollaborator(@Param("userId") Long userId, @Param("taskId") Long taskId);

}