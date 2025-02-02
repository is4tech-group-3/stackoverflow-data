package com.stackoverflow.service.answer;

import com.stackoverflow.bo.Answer;
import com.stackoverflow.bo.Question;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final Validator validator;

    private static final String QUESTION_NOT_FOUND = "Question not found with ID: ";
    private static final String ANSWER_NOT_FOUND = "Answer not found with ID: ";
    private static final String USER_NOT_FOUND = "User not found with ID: ";
    private static final String ADMIN = "ROLE_ADMIN";

    @Override
    public AnswerResponse createAnswer(Long idQuestion, AnswerRequest answerRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long userId = ((User) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + userId));
        if (!questionRepository.existsById(idQuestion)) {
            throw new EntityNotFoundException(QUESTION_NOT_FOUND + idQuestion);
        }
        Answer answer = Answer.builder()
                .description(answerRequest.getDescription())
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .verified(false)
                .idQuestion(idQuestion)
                .user(user)
                .build();
        answerRepository.save(answer);
        return createAnswerResponse(answer);
    }

    @Override
    public Page<AnswerResponse> getAnswersByQuestionId(Long idQuestion, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Answer> answerPage = answerRepository.findByIdQuestion(idQuestion, pageable);

        return answerPage.map(this::createAnswerResponse);
    }

    @Override
    public AnswerResponse findAnswerById(Long idAnswer) {
        Answer answer = answerRepository.findById(idAnswer)
                .orElseThrow(() -> new EntityNotFoundException(
                        ANSWER_NOT_FOUND + idAnswer));
        return createAnswerResponse(answer);
    }

    @Override
    public AnswerResponse updateAnswer(Long idAnswer, AnswerRequest answerRequest) {
        Answer answer = answerRepository.findById(idAnswer)
                .orElseThrow(() -> new EntityNotFoundException(ANSWER_NOT_FOUND + idAnswer));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        if (!Objects.equals(answer.getUser().getId(), userId) && !roles.contains(ADMIN)) {
            throw new AccessDeniedException("You do not have permission to edit this answer");
        }

        answer.setDescription(answerRequest.getDescription());
        answer.setDateUpdated(LocalDateTime.now());

        Set<ConstraintViolation<Answer>> violations = validator.validate(answer);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        answerRepository.save(answer);

        return createAnswerResponse(answer);
    }

    @Override
    public void deleteAnswer(Long idAnswer) {
        Answer answer = answerRepository.findById(idAnswer)
                .orElseThrow(() -> new EntityNotFoundException(ANSWER_NOT_FOUND + idAnswer));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        if (!Objects.equals(answer.getUser().getId(), userId) && !roles.contains(ADMIN)) {
            throw new AccessDeniedException("You do not have permission to delete this answer");
        }

        answerRepository.deleteById(idAnswer);
    }

    @Override
    public void verifiedAnswer(Long idQuestion, Long idAnswer) {
        answerRepository.findByIdQuestionAndVerifiedTrue(idQuestion)
                .ifPresent(answer -> {
                    throw new DataIntegrityViolationException("This question already has a verified answer");
                });
        Answer answer = answerRepository.findByIdAnswerAndIdQuestion(idAnswer, idQuestion)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No answer was found with id " + idAnswer + " pertaining to the question with id " + idQuestion));
        Question question = questionRepository.findById(answer.getIdQuestion())
                .orElseThrow(() -> new EntityNotFoundException(QUESTION_NOT_FOUND + answer.getIdQuestion()));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(question.getUser().getId(), userId) && !roles.contains(ADMIN))
            throw new AccessDeniedException("Do not have permission to verify this answer");

        answer.setVerified(true);
        answerRepository.save(answer);
    }

    @Override
    public void removeVerifiedAnswer(Long idQuestion, Long idAnswer) {
        Answer answer = answerRepository.findByIdQuestionAndIdAnswerAndVerifiedTrue(idQuestion, idAnswer)
                .orElseThrow(() -> new EntityNotFoundException("the answer with the id " + idAnswer + " is not verified"));
        Question question = questionRepository.findById(answer.getIdQuestion())
                .orElseThrow(() -> new EntityNotFoundException(QUESTION_NOT_FOUND + answer.getIdQuestion()));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(question.getUser().getId(), userId) && !roles.contains(ADMIN))
            throw new AccessDeniedException("Do not have permission to remove verification from this answer");
        answer.setVerified(false);
        answerRepository.save(answer);
    }

    @Override
    public AnswerResponse getAnswerVerifiedByQuestionId(Long idQuestion) {
        Answer answer = answerRepository.findByIdQuestionAndVerifiedTrue(idQuestion)
                .orElseThrow(() -> new EntityNotFoundException("The verified answer to the question with id " + idQuestion + " was not found"));
        return createAnswerResponse(answer);
    }

    public AnswerResponse createAnswerResponse(Answer answer) {
        return AnswerResponse.builder()
                .idAnswer(answer.getIdAnswer())
                .description(answer.getDescription())
                .dateCreated(answer.getDateCreated())
                .dateUpdated(answer.getDateUpdated())
                .verified(answer.getVerified())
                .idQuestion(answer.getIdQuestion())
                .author(
                        new UserResponse(
                                answer.getUser().getId(),
                                answer.getUser().getName(),
                                answer.getUser().getSurname(),
                                answer.getUser().getUsername(),
                                answer.getUser().getImage()
                        )
                )
                .build();
    }
}
