package com.precious.TaskApi.service;

import com.precious.TaskApi.model.Review;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.repository.ReviewRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private static final double MIN_RATING_FOR_CRITICAL_TASKS = 4.0;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review updateReview(Long userId, Long taskId, Double rating, String feedback) {
        Review userRating = new Review();
        userRating.setUserId(userId);
        userRating.setTaskId(taskId);
        userRating.addRating(rating);
        userRating.setFeedback(feedback);
        userRating.setCreatedAt(LocalDateTime.now());

        reviewRepository.findAverageRatingByUserId(userId);
        return reviewRepository.save(userRating);
    }

    public Double getUserRating(Long userId) {
        return reviewRepository.findAverageRatingByUserId(userId);
    }

    public boolean isEligibleForTask(Long userId, Task task) {
        Double userRating = getUserRating(userId);
        return task.getPriority() < 3 || userRating >= MIN_RATING_FOR_CRITICAL_TASKS;
    }
}