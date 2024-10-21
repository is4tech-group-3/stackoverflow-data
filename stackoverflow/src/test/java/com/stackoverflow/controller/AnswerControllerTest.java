package com.stackoverflow.controller;

import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;
import com.stackoverflow.service.answer.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class AnswerControllerTest {

    @InjectMocks
    private AnswerController answerController;

    @Mock
    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testCreateAnswer() {
        Long idQuestion = 1L;
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setDescription("New Answer");

        AnswerResponse answerResponse = AnswerResponse.builder()
                .idAnswer(1L)
                .description("New Answer")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .verified(false)
                .idQuestion(idQuestion)
                .build();

        when(answerService.createAnswer(eq(idQuestion), any(AnswerRequest.class))).thenReturn(answerResponse);

        ResponseEntity<AnswerResponse> response = answerController.createAnswer(idQuestion, answerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(answerResponse, response.getBody());
    }

    @Test
    void testGetAnswer() {
        Long idAnswer = 1L;
        AnswerResponse answerResponse = AnswerResponse.builder()
                .idAnswer(idAnswer)
                .description("Sample Answer")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .verified(false)
                .build();

        when(answerService.findAnswerById(idAnswer)).thenReturn(answerResponse);

        ResponseEntity<AnswerResponse> response = answerController.findAnswerById(idAnswer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(answerResponse, response.getBody());
    }

    @Test
    void testGetAnswerNotFound() {
        Long idAnswer = 2L;

        when(answerService.findAnswerById(idAnswer)).thenReturn(null);

        ResponseEntity<AnswerResponse> response = answerController.findAnswerById(idAnswer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void testUpdateAnswer() {
        Long idAnswer = 1L;
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setDescription("Updated Answer");

        AnswerResponse answerResponse = AnswerResponse.builder()
                .idAnswer(idAnswer)
                .description("Updated Answer")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .verified(false)
                .build();

        when(answerService.updateAnswer(eq(idAnswer), any(AnswerRequest.class))).thenReturn(answerResponse);

        ResponseEntity<AnswerResponse> response = answerController.updateAnswer(idAnswer, answerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(answerResponse, response.getBody());
    }

    @Test
    void testDeleteAnswer() {
        Long idAnswer = 1L;

        ResponseEntity<String> response = answerController.deleteAnswer(idAnswer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Answer deleted successfully", response.getBody());

        verify(answerService, times(1)).deleteAnswer(idAnswer);
    }

}
