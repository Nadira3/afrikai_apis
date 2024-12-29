package com.precious.TaskApi.controller;

import com.precious.TaskApi.dto.task.ExamSubmissionRequest;
import com.precious.TaskApi.service.exam.ExamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// @RestController
// @RequestMapping("/api/exams")
// @Slf4j
// @RequiredArgsConstructor
// public class ExamController {
//     private final ExamService examService;

//     @PostMapping("/{examId}/submit")
//     public ResponseEntity<ExamResult> submitExam(
//             @PathVariable Long examId,
//             @RequestBody ExamSubmissionRequest request) {
//         return ResponseEntity.ok(examService.submitExam(examId, request));
//     }
// }
