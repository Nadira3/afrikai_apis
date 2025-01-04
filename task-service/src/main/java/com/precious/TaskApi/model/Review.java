package com.precious.TaskApi.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // ID of the user being reviewed (Tasker)

    @Column(name = "task_id")
    private Long taskId; // ID of the task being reviewed

    @Column(name = "reviewer_id")
    private Long reviewerId; // ID of the reviewer

    private Double rating = 0.0; // Rating (out of 5 or any other scale)

    private List<Double> ratings; // List of ratings for the task

    @Column(columnDefinition = "TEXT")
    private String feedback; // Optional feedback from the reviewer

    @Column(name = "reviewed_type")
    private String reviewType; // Could be "Training", "Exam", or "Task"

    @Column(name = "reviewer_role")
    private String reviewerRole; // Role of the reviewer (Admin, Tasker, etc.)

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Date and time when the review was created

    public void addRating(Double rating) {
        this.ratings.add(rating);
    }
}
