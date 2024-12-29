package com.precious.TaskApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.precious.TaskApi.model.task.Training;

public interface TrainingRepository extends JpaRepository<Training, Long>, PagingAndSortingRepository<Training, Long>{

}
