package com.precious.TaskApi.model.content;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QuestionAnswerResponse {
    private final String question;
    private final String answer;
    
    @Override
    public String toString() {
        return String.format("Q: %s | A: %s", question, answer);
    }
}
