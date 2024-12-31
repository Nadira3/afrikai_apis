package com.precious.TaskApi.model.content;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QuestionAnswerResponse {
    private final String question;
    private final String answer;
   
    // Override toString method to return the question and answer
    // in a readable json format
    @Override
    public String toString() {
					return "{" +
							"\"question\":\"" + question + "\"," +
							"\"answer\":\"" + answer + "\"" +
							"}";
    }
}
