package com.internship.todotask.task.repository;

import com.internship.todotask.task.model.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findTaskEntityById(Long id);

    Page<TaskEntity> findTaskEntitiesByOwnerId(Long ownerId, Pageable pageable);

    void deleteTaskEntityById(Long id);

}
