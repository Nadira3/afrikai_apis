package com.precious.TaskApi.service.web;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.precious.TaskApi.model.content.QuestionAnswerResponse;
import com.precious.TaskApi.model.enums.TaskCategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Updated TaskSpecificApiClient
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSpecificApiClient {
    private final WebClient webClient;
    
    public List<String> fetchQuestionsAndAnswers(TaskCategory category, int numberOfQuestions) {
        return webClient.get()
            .uri("/api/{category}/questions?count={count}", 
                category.toString().toLowerCase(),
                numberOfQuestions)
            .retrieve()
            .bodyToFlux(QuestionAnswerResponse.class)
            .map(QuestionAnswerResponse::toString)
            .collectList()
            .block();
    }
}