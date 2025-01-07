// package com.precious.TaskApi.model.task;

// import static org.assertj.core.api.Assertions.assertThat;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.UUID;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;


// import com.precious.TaskApi.model.enums.TaskCategory;
// import com.precious.TaskApi.model.enums.TaskStatus;


// class TaskTest {
    
//     // Concrete implementation of Task for testing
//     private static class TestTask extends Task {
//         public TestTask() {
//             super();
//         }
//     }

//     private TestTask task;
//     private LocalDateTime now;

//     @BeforeEach
//     void setUp() {
//         now = LocalDateTime.now();
//         task = new TestTask();
//         task.setId(UUID.randomUUID()); // set random ID
//         task.setClientId("client123");
//         task.setTitle("Test Task");
//         task.setDescription("Test Description");
//         task.setReward(100.0);
//         task.setCreatedAt(now);
//         task.setCategory(TaskCategory.LABEL);
//         task.setCreatedBy(1L);
//     }

//     @Test
//     void testDefaultValues() {
//         TestTask newTask = new TestTask();
//         assertThat(newTask.getStatus()).isEqualTo(TaskStatus.CREATED);
//         assertThat(newTask.getPriority()).isEqualTo(0);
//     }

//     @Test
//     void testBasicProperties() {
//         assertThat(task.getClientId()).isEqualTo("client123");
//         assertThat(task.getTitle()).isEqualTo("Test Task");
//         assertThat(task.getDescription()).isEqualTo("Test Description");
//         assertThat(task.getReward()).isEqualTo(100.0);
//         assertThat(task.getCreatedAt()).isEqualTo(now);
//         assertThat(task.getCategory()).isEqualTo(TaskCategory.LABEL);
//         assertThat(task.getCreatedBy()).isEqualTo(1L);
//     }

//     @Test
//     void testStatusTransitions() {
//         assertThat(task.getStatus()).isEqualTo(TaskStatus.CREATED);
        
//         task.setStatus(TaskStatus.IN_PROGRESS);
//         assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        
//         task.setStatus(TaskStatus.COMPLETED);
//         assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
//     }

//     @Test
//     void testTimestamps() {
//         LocalDateTime assignedTime = now.plusHours(1);
//         LocalDateTime completedTime = now.plusHours(2);
        
//         task.setAssignedAt(assignedTime);
//         task.setCompletedAt(completedTime);
        
//         assertThat(task.getAssignedAt()).isEqualTo(assignedTime);
//         assertThat(task.getCompletedAt()).isEqualTo(completedTime);
//     }

//     @Test
//     void testDurationAndDeadline() {
//         LocalDateTime deadline = now.plusDays(7);
//         Duration duration = Duration.ofHours(2);
        
//         task.setDeadline(deadline);
//         task.setDurationPerTask(duration);
        
//         assertThat(task.getDeadline()).isEqualTo(deadline);
//         assertThat(task.getDurationPerTask()).isEqualTo(duration);
//     }

// }