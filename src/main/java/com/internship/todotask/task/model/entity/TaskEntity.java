package com.internship.todotask.task.model.entity;

import com.internship.todotask.task.model.dictionary.Priority;
import com.internship.todotask.task.model.dictionary.PriorityConverter;
import com.internship.todotask.task.model.dictionary.State;
import com.internship.todotask.task.model.dictionary.StateConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "priority", nullable = false)
    @Convert(converter = PriorityConverter.class)
    private Priority priority;

    @Column(name = "state", nullable = false)
    @Convert(converter = StateConverter.class)
    private State state;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

}
