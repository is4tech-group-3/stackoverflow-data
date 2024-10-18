package com.stackoverflow.service.question;

import com.stackoverflow.bo.Question;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.question.QuestionRequest;
import com.stackoverflow.dto.question.QuestionResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final Validator validator;

    private static final String QUESTION_NOT_FOUND = "Question not found with ID: ";
    private static final String USER_NOT_FOUND = "User not found with ID: ";
    private static final String ADMIN = "ROLE_ADMIN";

    @Override
    public Page<QuestionResponse> getAllQuestions(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Question> questions = questionRepository.findAll(pageable);
        return questions.map(this::createQuestionResponse);
    }

    @Override

    public QuestionResponse getQuestion(Long idQuestion) {
        Question question = questionRepository.findById(idQuestion)
                .orElseThrow(() -> new EntityNotFoundException(QUESTION_NOT_FOUND + idQuestion));
        return createQuestionResponse(question);
    }

    @Override
    public QuestionResponse createQuestion(QuestionRequest questionRequest) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(questionRequest.getIdTags()));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + userId));

        Question question = Question.builder()
                .title(questionRequest.getTitle())
                .description(questionRequest.getDescription())
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .user(user)
                .tags(tags)
                .build();

        questionRepository.save(question);

        return createQuestionResponse(question);
    }

    @Override
    public QuestionResponse updateQuestion(Long idQuestion, QuestionRequest questionRequest) {
        Question question = questionRepository.findById(idQuestion)
                .orElseThrow(() -> new EntityNotFoundException(QUESTION_NOT_FOUND + idQuestion));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long userId = ((User) userDetails).getId();

        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        if (!Objects.equals(question.getUser().getId(), userId) && !roles.contains(ADMIN)) {
            throw new AccessDeniedException("You do not have permission to edit this question");
        }

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(questionRequest.getIdTags()));
        question.setTitle(questionRequest.getTitle());
        question.setDescription(questionRequest.getDescription());
        question.setTags(tags);
        question.setDateUpdate(LocalDateTime.now());

        Set<ConstraintViolation<Question>> violations = validator.validate(question);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);

        questionRepository.save(question);

        return createQuestionResponse(question);
    }

    @Override
    public void deleteQuestion(Long idQuestion) {
        Question question = questionRepository.findById(idQuestion)
                .orElseThrow(() -> new EntityNotFoundException(QUESTION_NOT_FOUND + idQuestion));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(question.getUser().getId(), userId) && !roles.contains(ADMIN)) {
            throw new AccessDeniedException("You do not have permission to edit this question");
        }
        questionRepository.deleteById(idQuestion);
    }

    public QuestionResponse createQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .idQuestion(question.getIdQuestion())
                .title(question.getTitle())
                .description(question.getDescription())
                .dateCreation(question.getDateCreation())
                .dateUpdate(question.getDateUpdate())
                .author(
                        new UserResponse(
                                question.getUser().getId(),
                                question.getUser().getName(),
                                question.getUser().getSurname(),
                                question.getUser().getUsername(),
                                question.getUser().getImage()
                        )
                )
                .tags(question.getTags())
                .build();
    }
}
