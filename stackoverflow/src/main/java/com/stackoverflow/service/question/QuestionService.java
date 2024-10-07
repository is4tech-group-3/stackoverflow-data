package com.stackoverflow.service.question;

import com.stackoverflow.bo.Question;
import com.stackoverflow.dto.question.QuestionRequest;

import org.springframework.data.domain.Page;

public interface QuestionService {
    Page<Question> getAllQuestions(int page, int size);
    Question getQuestion(Long idQuestion);
    Question createQuestion(QuestionRequest questionRequest);
    Question updateQuestion(Long idQuestion, QuestionRequest questionRequest);
    void deleteQuestion(Long idQuestion);
}

