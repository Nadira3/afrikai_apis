package com.precious.TaskApi.service.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import com.precious.TaskApi.model.QueryResponse;

@Service
public class ExternalEntityService {

    private final WebClient webClient;

    public ExternalEntityService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080") // Replace with actual LabelAPI base URL
                .build();
    }

    public Mono<QueryResponse> uploadAndProcessFile(String category, MultipartFile file, UUID clientId) {
        try {
            // Prepare the multipart form data, including file and clientId
            return webClient.post()
                    .uri("/api/{category}/upload", category)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(Map.of(
                            "file", file.getResource(),
                            "clientId", clientId.toString() // Passing clientId as a String
                    ))
                    .retrieve()
                    .bodyToMono(QueryResponse.class) // Adjust the type based on LabelAPI's response
                    .doOnSuccess(response -> {
                        System.out.println("File processed successfully: " + response);
                    })
                    .doOnError(error -> {
                        System.err.println("Error during file processing: " + error.getMessage());
                    });

        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to process file", e));
        }
    }
}

