package com.precious.TaskApi.service.web;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.precious.TaskApi.model.content.QuestionAnswerResponse;
import com.precious.TaskApi.model.enums.TaskCategory;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import lombok.extern.slf4j.Slf4j;

// Updated TaskSpecificApiClient
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSpecificApiClient {

   @Qualifier("userApiClient")
    private final WebClient userApiClient;

    @Qualifier("labelApiClient")
    private final WebClient labelApiClient;

    public List<String> fetchQuestionsAndAnswers(TaskCategory category, int numberOfQuestions) {
        WebClient selectedClient = selectClient(category);  // Select the WebClient based on category
        
        return selectedClient.get()
            .uri("/api/{category}/questions?count={count}", 
                category.toString().toLowerCase(),
                numberOfQuestions)
            .retrieve()
            .bodyToFlux(QuestionAnswerResponse.class)
            .map(QuestionAnswerResponse::toString)
            .collectList()
            .block();
    }

    private WebClient selectClient(TaskCategory category) {
        switch (category) {
            case ENTRY:
                return labelApiClient;
            case LABEL:
                return labelApiClient;
            default:
                throw new IllegalArgumentException("Unknown TaskCategory: " + category);
        }
    }
}
