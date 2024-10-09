package com.stackoverflow.service.answer;

import com.stackoverflow.bo.Answer;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AnswerServiceImpl implements AnswerService {

        private final AnswerRepository answerRepository;
        private final UserRepository userRepository;
        private final QuestionRepository questionRepository;

        @Override
        public AnswerResponse createAnswer(Long idQuestion, AnswerRequest answerRequest) {
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                Long userId = ((User) userDetails).getId();
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                questionRepository.findById(idQuestion)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Question not found with id: " + idQuestion));
                Answer answer = Answer.builder()
                                .description(answerRequest.getDescription())
                                .dateCreated(LocalDateTime.now())
                                .dateUpdated(LocalDateTime.now())
                                .likes(0)
                                .verified(false)
                                .idQuestion(idQuestion)
                                .user(user)
                                .build();
                answerRepository.save(answer);
                return createAnswerResponse(answer);
        }

        @Override
        public Page<AnswerResponse> getAnswersByQuestionId(Long idQuestion, int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Answer> answerPage = answerRepository.findByIdQuestion(idQuestion, pageable);

                return answerPage.map(this::createAnswerResponse);
        }

        @Override
        public AnswerResponse findAnswerById(Long idAnswer) {
                Answer answer = answerRepository.findById(idAnswer)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Answer not found with id: " + idAnswer));
                return createAnswerResponse(answer);
        }

    @Override
    public AnswerResponse updateAnswer(Long idAnswer, AnswerRequest answerRequest) {
        Answer answer = answerRepository.findById(idAnswer)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + idAnswer));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(answer.getUser().getId(), userId) && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("You do not have permission to edit this answer");
        }
        answer.setDescription(answerRequest.getDescription());
        answer.setDateUpdated(LocalDateTime.now());
        answerRepository.save(answer);
        return createAnswerResponse(answer);
    }

    @Override
    public void deleteAnswer(Long idAnswer) {
        Answer answer = answerRepository.findById(idAnswer)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + idAnswer));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(answer.getUser().getId(), userId) && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("You do not have permission to delete this answer");
        }
        answerRepository.deleteById(idAnswer);
    }

        public AnswerResponse createAnswerResponse(Answer answer) {
                return AnswerResponse.builder()
                                .idAnswer(answer.getIdAnswer())
                                .description(answer.getDescription())
                                .dateCreated(answer.getDateCreated())
                                .dateUpdated(answer.getDateUpdated())
                                .likes(answer.getLikes())
                                .verified(answer.getVerified())
                                .idQuestion(answer.getIdQuestion())
                                .author(
                                                new UserResponse(
                                                                answer.getUser().getId(),
                                                                answer.getUser().getName(),
                                                                answer.getUser().getSurname(),
                                                                answer.getUser().getUsername()))
                                .build();
        }
}
