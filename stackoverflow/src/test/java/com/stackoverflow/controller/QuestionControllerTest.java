package com.stackoverflow.controller;

import com.stackoverflow.dto.question.QuestionRequest;
import com.stackoverflow.dto.question.QuestionResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.service.question.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class QuestionControllerTest {

    @InjectMocks
    private QuestionController questionController;

    @Mock
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testGetQuestion() {
        QuestionResponse questionResponse = QuestionResponse.builder()
                .idQuestion(1L)
                .title("Sample Title")
                .description("Sample Description")
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .tags(Collections.emptySet())
                .build();

        when(questionService.getQuestion(1L)).thenReturn(questionResponse);

        ResponseEntity<QuestionResponse> response = questionController.getQuestion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questionResponse, response.getBody());
    }

    @Test
    void testGetAllQuestions() {
        QuestionResponse questionResponse = QuestionResponse.builder()
                .idQuestion(1L)
                .title("Sample Title")
                .description("Sample Description")
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .tags(Collections.emptySet())
                .build();

        Page<QuestionResponse> questionPage = new PageImpl<>(List.of(questionResponse));

        when(questionService.getAllQuestions(0, 10, "dateCreation", "desc")).thenReturn(questionPage);

        Page<QuestionResponse> result = questionController.getQuestions(0, 10, "dateCreation", "desc");

        assertEquals(1, result.getTotalElements());
        assertEquals(questionResponse, result.getContent().get(0));
    }

    @Test
    void testCreateQuestion() {
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setTitle("New Question");
        questionRequest.setDescription("New Description");
        questionRequest.setIdTags(Collections.emptySet());

        QuestionResponse questionResponse = QuestionResponse.builder()
                .idQuestion(1L)
                .title("New Question")
                .description("New Description")
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .tags(Collections.emptySet())
                .build();

        when(questionService.createQuestion(questionRequest)).thenReturn(questionResponse);

        ResponseEntity<QuestionResponse> response = questionController.createQuestion(questionRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(questionResponse, response.getBody());
    }

    @Test
    void testUpdateQuestion() {
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setTitle("Updated Question");
        questionRequest.setDescription("Updated Description");
        questionRequest.setIdTags(Collections.emptySet());

        QuestionResponse questionResponse = QuestionResponse.builder()
                .idQuestion(1L)
                .title("Updated Question")
                .description("Updated Description")
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .tags(Collections.emptySet())
                .build();

        when(questionService.updateQuestion(1L, questionRequest)).thenReturn(questionResponse);

        ResponseEntity<QuestionResponse> response = questionController.updateQuestion(1L, questionRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questionResponse, response.getBody());
    }

    @Test
    void testDeleteQuestion() {
        ResponseEntity<String> response = questionController.deleteQuestion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Question deleted successfully", response.getBody());

        verify(questionService, times(1)).deleteQuestion(1L);
    }
}
