package com.precious.TaskApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.precious.TaskApi.model.content.ExamContent;

public interface ExamContentRepository extends JpaRepository<ExamContent, Long>, PagingAndSortingRepository<ExamContent, Long>{
    
}

