package com.precious.TaskApi.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.task.Task;

public interface TaskRepository extends JpaRepository<Task, UUID>, PagingAndSortingRepository<Task, UUID>{

    List<Task> findAllByDeadlineBeforeAndStatusNot(LocalDateTime currentTime, TaskStatus expired);

}