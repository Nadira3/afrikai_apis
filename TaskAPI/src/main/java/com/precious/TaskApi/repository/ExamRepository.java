package com.precious.TaskApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.precious.TaskApi.model.task.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long>, PagingAndSortingRepository<Exam, Long>{
}

