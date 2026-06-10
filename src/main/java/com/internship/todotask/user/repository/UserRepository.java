package com.internship.todotask.user.repository;

import com.internship.todotask.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.*;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAll(Pageable pageable);

    Optional<UserEntity> findUserEntityById(Long id);

    @Query(value = "SELECT user_id FROM user_task WHERE task_id = :taskId", nativeQuery = true)
    Set<Long> getUserIdsByTaskId(@Param("taskId") Long taskId);

}