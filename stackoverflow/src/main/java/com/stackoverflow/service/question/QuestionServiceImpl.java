package com.stackoverflow.service.question;

import com.stackoverflow.bo.Question;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.question.QuestionRequest;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Page<Question> getAllQuestions(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return questionRepository.findAll(pageable);
    }

    @Override
    public Question getQuestion(Long idQuestion) {
        return questionRepository.findById(idQuestion)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
    }

    @Override
    public Question createQuestion(QuestionRequest questionRequest) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(questionRequest.getIdTags()));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Question question = Question.builder()
                .title(questionRequest.getTitle())
                .description(questionRequest.getDescription())
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .user(user)
                .tags(tags)
                .build();
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long idQuestion, QuestionRequest questionRequest) {
        Question question = questionRepository.findById(idQuestion)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(questionRequest.getIdTags()));
        question.setTitle(questionRequest.getTitle());
        question.setDescription(questionRequest.getDescription());
        question.setTags(tags);
        question.setDateUpdate(LocalDateTime.now());
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long idQuestion) {
        questionRepository.findById(idQuestion)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        questionRepository.deleteById(idQuestion);
    }
}
