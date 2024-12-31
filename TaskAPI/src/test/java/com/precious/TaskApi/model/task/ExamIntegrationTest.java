// package com.precious.TaskApi.model.task;

// import static org.assertj.core.api.Assertions.assertThat;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.HashMap;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// import com.precious.TaskApi.model.content.ExamContent;
// import com.precious.TaskApi.model.enums.TaskCategory;
// import com.precious.TaskApi.model.enums.TaskType;

// @DataJpaTest
// class ExamIntegrationTest {

//     @Autowired
//     private TestEntityManager entityManager;

//     private Exam createExam() {
//         ExamContent examContent = new ExamContent();
//         examContent.setQuestions(Arrays.asList("Q1", "Q2", "Q3"));
//         examContent.setAnswers(Arrays.asList("A1", "A2", "A3"));
//         examContent.setPassingGrade(70);

//         Exam exam = new Exam();
//         exam.setClientId("client123");
//         exam.setTitle("Math Exam");
//         exam.setDescription("Final Math Examination");
//         exam.setReward(50.0);
//         exam.setCreatedAt(LocalDateTime.now());
//         exam.setCreatedBy(1L);
//         exam.setCategory(TaskCategory.DATA_LABELING);
//         exam.setType(TaskType.EXAM);
//         exam.setContent("Exam content");
//         exam.setDuration(Duration.ofHours(2));
//         exam.setPassingGrade(70.0);
//         exam.setParticipantScores(new HashMap<>());
//         exam.setExamContent(examContent);

//         return exam;
//     }

//     @Test
//     void testExamPersistence() {
//         // Create and persist an exam
//         Exam exam = createExam();
//         Exam savedExam = entityManager.persist(exam);
//         entityManager.flush();
//         entityManager.clear();

//         // Retrieve the exam and verify its properties
//         Exam retrievedExam = entityManager.find(Exam.class, savedExam.getId());
//         assertThat(retrievedExam).isNotNull();
//         assertThat(retrievedExam.getExamContent()).isNotNull();
//         assertThat(retrievedExam.getExamContent().getQuestions()).hasSize(3);
//         assertThat(retrievedExam.getExamContent().getAnswers()).hasSize(3);
//     }

//     @Test
//     void testCascadeDelete() {
//         // Create and persist an exam
//         Exam exam = createExam();
//         Exam savedExam = entityManager.persist(exam);
//         Long examContentId = savedExam.getExamContent().getId();
//         entityManager.flush();
//         entityManager.clear();

//         // Delete the exam
//         Exam retrievedExam = entityManager.find(Exam.class, savedExam.getId());
//         entityManager.remove(retrievedExam);
//         entityManager.flush();
//         entityManager.clear();

//         // Verify that both exam and exam content are deleted
//         assertThat(entityManager.find(Exam.class, savedExam.getId())).isNull();
//         assertThat(entityManager.find(ExamContent.class, examContentId)).isNull();
//     }

//     @Test
//     void testExamContentQuestionAndAnswerPersistence() {
//         // Create and persist an exam with questions and answers
//         Exam exam = createExam();
//         exam.getExamContent().setQuestions(Arrays.asList("Q1", "Q2", "Q3"));
//         exam.getExamContent().setAnswers(Arrays.asList("A1", "A2", "A3"));
        
//         Exam savedExam = entityManager.persist(exam);
//         entityManager.flush();
//         entityManager.clear();

//         // Retrieve and verify questions and answers
//         ExamContent retrievedContent = entityManager.find(Exam.class, savedExam.getId()).getExamContent();
//         assertThat(retrievedContent.getQuestions())
//             .hasSize(3)
//             .containsExactly("Q1", "Q2", "Q3");
//         assertThat(retrievedContent.getAnswers())
//             .hasSize(3)
//             .containsExactly("A1", "A2", "A3");
//     }

//     @Test
//     void testUpdateExamContent() {
//         // Create and persist an exam
//         Exam exam = createExam();
//         Exam savedExam = entityManager.persist(exam);
//         entityManager.flush();

//         // Update exam content
//         savedExam.getExamContent().setQuestions(Arrays.asList("Updated Q1", "Updated Q2"));
//         savedExam.getExamContent().setAnswers(Arrays.asList("Updated A1", "Updated A2"));
//         savedExam.getExamContent().setPassingGrade(75);
//         entityManager.flush();
//         entityManager.clear();

//         // Verify updates
//         ExamContent retrievedContent = entityManager.find(Exam.class, savedExam.getId()).getExamContent();
//         assertThat(retrievedContent.getQuestions())
//             .hasSize(2)
//             .containsExactly("Updated Q1", "Updated Q2");
//         assertThat(retrievedContent.getAnswers())
//             .hasSize(2)
//             .containsExactly("Updated A1", "Updated A2");
//         assertThat(retrievedContent.getPassingGrade()).isEqualTo(75);
//     }
// }