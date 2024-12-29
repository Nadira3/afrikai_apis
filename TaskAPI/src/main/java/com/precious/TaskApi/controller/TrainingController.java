package com.precious.TaskApi.controller;

import com.precious.TaskApi.dto.task.TrainingCompleteRequest;
import com.precious.TaskApi.service.training.TrainingService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/training")
@Slf4j
public class TrainingController {
    private final TrainingService trainingService;

    TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/{trainingId}/complete")
    public ResponseEntity<Void> completeTraining(@RequestBody TrainingCompleteRequest request) {
        trainingService.completeTraining(request);
        return ResponseEntity.ok().build();
    }
}
