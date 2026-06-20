package com.internship.todotask.task.repository;

import com.internship.todotask.task.model.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findTaskEntityById(Long id);

    List<TaskEntity> findTaskEntitiesByOwnerId(Long ownerId);

    Page<TaskEntity> findTaskEntitiesByOwnerId(Long ownerId, Pageable pageable);

    @Query(value = "SELECT t FROM tasks t WHERE t.id IN " +
            "(SELECT ct.task_id FROM collaborators_tasks ct WHERE ct.user_id = :userId)", nativeQuery = true)
    Page<TaskEntity> findTaskEntitiesByCollaboratorId(@Param("userId") Long userId, Pageable pageable);

    void deleteTaskEntityById(Long id);

}
