package com.precious.TaskApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.precious.TaskApi.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, PagingAndSortingRepository<Review, Long>{
    Double findAverageRatingByUserId(Long userId);
}
