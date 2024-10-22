package com.stackoverflow.service.question;

import com.stackoverflow.dto.question.QuestionRequest;
import com.stackoverflow.dto.question.QuestionResponse;

import org.springframework.data.domain.Page;

public interface QuestionService {
    Page<QuestionResponse> getAllQuestions(int page, int size, String sortBy, String sortDirection);
    QuestionResponse getQuestion(Long idQuestion);
    QuestionResponse createQuestion(QuestionRequest questionRequest);
    QuestionResponse updateQuestion(Long idQuestion, QuestionRequest questionRequest);
    void deleteQuestion(Long idQuestion);
}

