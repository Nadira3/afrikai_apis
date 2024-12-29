package com.precious.TaskApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.precious.TaskApi.model.task.WorkTask;

public interface WorkTaskRepository extends JpaRepository<WorkTask, Long>, PagingAndSortingRepository<WorkTask, Long>{

}
