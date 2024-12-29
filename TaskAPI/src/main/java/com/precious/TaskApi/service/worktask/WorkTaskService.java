package com.precious.TaskApi.service.worktask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.model.content.TaskContent;
import com.precious.TaskApi.model.task.WorkTask;
import com.precious.TaskApi.service.NotificationService;
import com.precious.TaskApi.service.exam.ExamContentService;
import com.precious.TaskApi.repository.WorkTaskRepository;
import com.precious.TaskApi.model.content.WorkContent;

public class WorkTaskService {
    private final WorkTaskRepository workTaskRepository;
    private final NotificationService notificationService;
    private final WebClient taskSpecificApiClient;
    private final ExamContentService examContentService;


     @Autowired
    public WorkTaskService
    (
        ExamContentService examContentService,
        WorkTaskRepository workTaskRepository, 
        NotificationService notificationService, 
        WebClient taskSpecificApiClient
        ) {
            this.examContentService = examContentService;
        this.workTaskRepository = workTaskRepository;
        this.notificationService = notificationService;
        this.taskSpecificApiClient = taskSpecificApiClient;
    }

    @Transactional
    public WorkTask createWorkTask (TaskCreationDto request) {
        // Fetch training content from task-specific API
        TaskContent content = examContentService.createExamContent(request);
        WorkTask workTask = new WorkTask();
        workTask.setContent((WorkContent) content);

        // Save workTask
        return workTaskRepository.save(workTask);
    
    }

}
