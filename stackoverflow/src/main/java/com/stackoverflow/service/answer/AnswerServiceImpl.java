package com.stackoverflow.service.answer;

import com.stackoverflow.bo.Answer;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.util.ValidationUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class AnswerServiceImpl implements AnswerService {

        private final AnswerRepository answerRepository;
        private final UserRepository userRepository;
        private final QuestionRepository questionRepository;

        @Override
        public AnswerResponse createAnswer(Long idQuestion, AnswerRequest answerRequest) {
                ValidationUtil.validateNotEmpty(answerRequest.getDescription(), "Description");
                ValidationUtil.validateMaxLength(answerRequest.getDescription(), 255, "Description");

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
                                                "Answer not found with id: " + idAnswer));
                return createAnswerResponse(answer);
        }

        @Override
        public AnswerResponse updateAnswer(Long idAnswer, AnswerRequest answerRequest) {
                ValidationUtil.validateNotEmpty(answerRequest.getDescription(), "Description");
                ValidationUtil.validateMaxLength(answerRequest.getDescription(), 255, "Description");

                Answer answer = answerRepository.findById(idAnswer)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Answer not found with id: " + idAnswer));
                answer.setDescription(answerRequest.getDescription());
                answer.setDateUpdated(LocalDateTime.now());
                answerRepository.save(answer);
                return createAnswerResponse(answer);
        }

        @Override
        public void deleteAnswer(Long idAnswer) {
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
