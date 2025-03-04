package com.precious.LabelAPI.controller;


import com.precious.LabelAPI.dto.LabelingSubmissionDto;
import com.precious.LabelAPI.dto.ReviewRequest;
import com.precious.LabelAPI.model.DataLabelingSubmission;
import com.precious.LabelAPI.service.LabelService;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/labels/user")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/home")
    public String getHomePage() {
           return "Welcome to Label API";
    }

    @PostMapping("/{taskId}/submit")
    public String submitLabel(@RequestBody LabelingSubmissionDto submission, @PathVariable UUID taskId) {
	DataLabelingSubmission response = labelService.submitLabel(submission, taskId);
	return response != null ? "Label submitted successfully" : "Failed to submit label";
    }

    @PostMapping("/{taskId}/review")
    public String reviewLabel(@RequestBody ReviewRequest review, @PathVariable UUID taskId) {
	    DataLabelingSubmission response = labelService.reviewLabel(review, taskId);
	    return response != null ? "Label reviewed successfully" : "Failed to review label";
    }

}
