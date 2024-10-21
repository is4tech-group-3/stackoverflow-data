package com.stackoverflow.service.answer;

import com.stackoverflow.bo.Answer;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createAnswerWhenValidInput() {
        Long idQuestion = 1L;
        AnswerRequest request = new AnswerRequest();
        request.setDescription("Test answer");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(questionRepository.existsById(idQuestion)).thenReturn(true);

        Answer answer = Answer.builder()
                .description("Test answer")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .verified(false)
                .idQuestion(idQuestion)
                .user(user)
                .build();

        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        AnswerResponse response = answerService.createAnswer(idQuestion, request);

        assertNotNull(response);
        assertEquals("Test answer", response.getDescription());
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void createAnswerWhenQuestionNotFound() {
        Long idQuestion = 1L;
        AnswerRequest request = new AnswerRequest();
        request.setDescription("Test answer");

        when(questionRepository.existsById(idQuestion)).thenReturn(false);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            answerService.createAnswer(idQuestion, request);
        });
        assertEquals("Question not found with ID: " + idQuestion, exception.getMessage());
    }

    @Test
    void createAnswerWhenUserNotFound() {
        Long idQuestion = 1L;
        AnswerRequest request = new AnswerRequest();
        request.setDescription("Test answer");

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(questionRepository.existsById(idQuestion)).thenReturn(true);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            answerService.createAnswer(idQuestion, request);
        });
        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateAnswerWhenValidInput() {
        Long idAnswer = 1L;
        AnswerRequest request = new AnswerRequest();
        request.setDescription("Updated answer");

        Answer existingAnswer = Answer.builder()
                .idAnswer(idAnswer)
                .description("Old answer")
                .user(user)
                .build();

        when(answerRepository.findById(idAnswer)).thenReturn(Optional.of(existingAnswer));

        AnswerResponse response = answerService.updateAnswer(idAnswer, request);

        assertNotNull(response);
        assertEquals("Updated answer", response.getDescription());
        verify(answerRepository).save(existingAnswer);
    }

    @Test
    void updateAnswerWhenAnswerNotFound() {
        Long idAnswer = 1L;
        AnswerRequest request = new AnswerRequest();
        request.setDescription("Updated answer");

        when(answerRepository.findById(idAnswer)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            answerService.updateAnswer(idAnswer, request);
        });
        assertEquals("Answer not found with ID: 1", exception.getMessage());
    }

    @Test
    void deleteAnswerWhenValidInput() {
        Long idAnswer = 1L;
        Answer existingAnswer = Answer.builder()
                .idAnswer(idAnswer)
                .user(user)
                .build();

        when(answerRepository.findById(idAnswer)).thenReturn(Optional.of(existingAnswer));

        assertDoesNotThrow(() -> answerService.deleteAnswer(idAnswer));
        verify(answerRepository).deleteById(idAnswer);
    }

    @Test
    void deleteAnswerWhenAnswerNotFound() {
        Long idAnswer = 1L;

        when(answerRepository.findById(idAnswer)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            answerService.deleteAnswer(idAnswer);
        });
        assertEquals("Answer not found with ID: 1", exception.getMessage());
    }

    @Test
    void verifiedAnswerWhenAnswerAlreadyVerified() {
        Long idQuestion = 1L;
        Long idAnswer = 2L;

        Answer answer = Answer.builder()
                .idAnswer(idAnswer)
                .verified(true)
                .idQuestion(idQuestion)
                .build();

        when(answerRepository.findByIdQuestionAndVerifiedTrue(idQuestion)).thenReturn(Optional.of(answer));

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            answerService.verifiedAnswer(idQuestion, idAnswer);
        });
        assertEquals("This question already has a verified answer", exception.getMessage());
    }

    @Test
    void verifiedAnswerWhenAnswerNotFound() {
        Long idQuestion = 1L;
        Long idAnswer = 2L;

        when(answerRepository.findByIdQuestionAndVerifiedTrue(idQuestion)).thenReturn(Optional.empty());
        when(answerRepository.findByIdAnswerAndIdQuestion(idAnswer, idQuestion)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            answerService.verifiedAnswer(idQuestion, idAnswer);
        });
        assertEquals("No answer was found with id 2 pertaining to the question with id 1", exception.getMessage());
    }

    @Test
    void getAnswerVerifiedByQuestionIdWhenFound() {
        Long idQuestion = 1L;
        Answer answer = Answer.builder()
                .idAnswer(2L)
                .verified(true)
                .idQuestion(idQuestion)
                .user(user)
                .build();

        when(answerRepository.findByIdQuestionAndVerifiedTrue(idQuestion)).thenReturn(Optional.of(answer));
        when(questionRepository.existsById(idQuestion)).thenReturn(true);

        AnswerResponse response = answerService.getAnswerVerifiedByQuestionId(idQuestion);

        assertNotNull(response);
        assertEquals(2L, response.getIdAnswer());
    }

    @Test
    void getAnswerVerifiedByQuestionIdWhenNotFound() {
        Long idQuestion = 1L;

        when(answerRepository.findByIdQuestionAndVerifiedTrue(idQuestion)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            answerService.getAnswerVerifiedByQuestionId(idQuestion);
        });
        assertEquals("The verified answer to the question with id 1 was not found", exception.getMessage());
    }

    @Test
    void updateAnswerWhenUserDoesNotOwnAnswer() {
        Long idAnswer = 1L;
        AnswerRequest request = new AnswerRequest();
        request.setDescription("Updated answer");

        Answer existingAnswer = Answer.builder()
                .idAnswer(idAnswer)
                .description("Old answer")
                .user(new User())
                .build();

        when(answerRepository.findById(idAnswer)).thenReturn(Optional.of(existingAnswer));

        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            answerService.updateAnswer(idAnswer, request);
        });
        assertEquals("You do not have permission to edit this answer", exception.getMessage());
    }

    @Test
    void deleteAnswerWhenUserDoesNotOwnAnswer() {
        Long idAnswer = 1L;
        Answer existingAnswer = Answer.builder()
                .idAnswer(idAnswer)
                .user(new User())
                .build();

        when(answerRepository.findById(idAnswer)).thenReturn(Optional.of(existingAnswer));

        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            answerService.deleteAnswer(idAnswer);
        });
        assertEquals("You do not have permission to delete this answer", exception.getMessage());
    }

    @Test
    void removeVerifiedAnswerWhenAnswerNotVerified() {
        Long idQuestion = 1L;
        Long idAnswer = 2L;

        Answer answer = Answer.builder()
                .idAnswer(idAnswer)
                .verified(false)
                .idQuestion(idQuestion)
                .build();

        when(answerRepository.findByIdQuestionAndIdAnswerAndVerifiedTrue(idQuestion, idAnswer))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            answerService.removeVerifiedAnswer(idQuestion, idAnswer);
        });
        assertEquals("the answer with the id " + idAnswer + " is not verified", exception.getMessage());
    }
}
