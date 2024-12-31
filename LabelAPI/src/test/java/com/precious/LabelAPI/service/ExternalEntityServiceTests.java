package com.precious.LabelAPI.service;



import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import com.precious.LabelAPI.dto.ClientReferenceDto;
import com.precious.LabelAPI.dto.TaskReferenceDto;
import com.precious.LabelAPI.model.UserRole;

import reactor.core.publisher.Mono;

@SpringBootTest
public class ExternalEntityServiceTests {

    

    @Autowired
    private ExternalEntityService externalEntityService;

    // Mocking the WebClient for taskServiceClient
    @MockBean
    @Qualifier("taskServiceClient")
    private WebClient taskServiceClient;

    // Mocking the WebClient for clientServiceClient
    @MockBean
    @Qualifier("clientServiceClient") // This is importnt because you ca nnot have more than one mockbean in a f
    private WebClient clientServiceClient;

    private UUID taskId;
    private UUID clientId;

    @BeforeEach
    public void setUp() {
        taskId = UUID.randomUUID();
        clientId = UUID.randomUUID();
    }

    @Test
    public void testGetTaskReference_success() {
        TaskReferenceDto mockTask = new TaskReferenceDto(clientId, "DATA LABELLING", "PENDING", LocalDateTime.now());
        
        // Mock the WebClient behavior for taskServiceClient
        when(taskServiceClient.get()
                .uri("/api/v1/tasks/{id}", taskId)
                .retrieve()
                .bodyToMono(TaskReferenceDto.class))
                .thenReturn(Mono.just(mockTask));

        // Test the service method
        Mono<TaskReferenceDto> taskMono = externalEntityService.getTaskReference(taskId);
        TaskReferenceDto taskResult = taskMono.block();

        assertNotNull(taskResult);
        verify(taskServiceClient).get();
    }

    @Test
    public void testGetClientReference_success() {
        ClientReferenceDto mockClient = new ClientReferenceDto(clientId, "John Doe", UserRole.ADMIN);

        // Mock the WebClient behavior for clientServiceClient
        when(clientServiceClient.get()
                .uri("/api/v1/clients/{id}", clientId)
                .retrieve()
                .bodyToMono(ClientReferenceDto.class))
                .thenReturn(Mono.just(mockClient));

        // Test the service method
        Mono<ClientReferenceDto> clientMono = externalEntityService.getClientReference(clientId);
        ClientReferenceDto clientResult = clientMono.block();

        assertNotNull(clientResult);
        verify(clientServiceClient).get();
    }
}
