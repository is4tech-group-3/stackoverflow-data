package com.stackoverflow.service.question;

import com.stackoverflow.bo.Question;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.question.QuestionRequest;
import com.stackoverflow.dto.question.QuestionResponse;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.question.QuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private User mockUser;
    private Question mockQuestion;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John");
        mockUser.setSurname("Doe");
        mockUser.setUsername("johndoe");

        mockQuestion = new Question();
        mockQuestion.setIdQuestion(1L);
        mockQuestion.setTitle("Sample Question");
        mockQuestion.setDescription("Sample Description");
        mockQuestion.setDateCreation(LocalDateTime.now());
        mockQuestion.setUser(mockUser);
        mockQuestion.setTags(new HashSet<>());
    }

    @Test
    public void testCreateQuestion() {
        QuestionRequest request = new QuestionRequest();
        request.setTitle("New Question");
        request.setDescription("New Description");
        request.setIdTags(Set.of(1L));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(tagRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(questionRepository.save(any(Question.class))).thenReturn(mockQuestion);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(mockUserDetails.getUsername()).thenReturn(mockUser.getUsername());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        QuestionResponse response = questionService.createQuestion(request);

        assertNotNull(response);
        assertEquals("New Question", response.getTitle());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    public void testGetQuestion() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(mockQuestion));

        QuestionResponse response = questionService.getQuestion(1L);

        assertNotNull(response);
        assertEquals(mockQuestion.getTitle(), response.getTitle());
        assertEquals(mockQuestion.getDescription(), response.getDescription());
    }

    @Test
    public void testGetQuestionNotFound() {
        when(questionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.getQuestion(2L));
    }

    @Test
    public void testUpdateQuestion() {
        QuestionRequest request = new QuestionRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setIdTags(Set.of(1L));

        when(questionRepository.findById(1L)).thenReturn(Optional.of(mockQuestion));
        when(tagRepository.findAllById(any())).thenReturn(new ArrayList<>());

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        QuestionResponse response = questionService.updateQuestion(1L, request);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        verify(questionRepository, times(1)).save(mockQuestion);
    }

    @Test
    public void testUpdateQuestionNotFound() {
        QuestionRequest request = new QuestionRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.updateQuestion(1L, request));
    }

    @Test
    public void testDeleteQuestion() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(mockQuestion));

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        questionService.deleteQuestion(1L);

        verify(questionRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteQuestionNotFound() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.deleteQuestion(1L));
    }

    @Test
    public void testGetAllQuestions() {
        List<Question> questions = List.of(mockQuestion);
        Page<Question> questionPage = new PageImpl<>(questions);
        when(questionRepository.findAll(any(Pageable.class))).thenReturn(questionPage);

        Page<QuestionResponse> responsePage = questionService.getAllQuestions(0, 10, "dateCreation", "asc");

        assertEquals(1, responsePage.getTotalElements());
        assertEquals(mockQuestion.getTitle(), responsePage.getContent().get(0).getTitle());
    }
}
