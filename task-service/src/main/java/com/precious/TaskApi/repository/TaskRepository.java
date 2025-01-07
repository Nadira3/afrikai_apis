package com.precious.TaskApi.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.task.Task;

public interface TaskRepository extends JpaRepository<Task, UUID>, PagingAndSortingRepository<Task, UUID>{

    List<Task> findAllByDeadlineBeforeAndStatusNot(LocalDateTime currentTime, TaskStatus expired);

    Page<Task> findAllByClientId(String clientId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE :userId MEMBER OF t.assignedUserIds")
    Page<Task> findByAssignedUserId(@Param("userId") Long userId, Pageable pageable);

    Page<Task> findByCategory(TaskCategory taskCategory, Pageable pageable);

    Page<Task> findByStatus(TaskStatus taskStatus, Pageable pageable);

    Page<Task> findByPriority(int priority, Pageable pageable);

}