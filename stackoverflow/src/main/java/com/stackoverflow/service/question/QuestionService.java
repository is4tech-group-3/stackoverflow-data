package com.stackoverflow.service.question;

import com.stackoverflow.bo.Question;
import com.stackoverflow.dto.question.QuestionRequest;

import java.util.List;

public interface QuestionService {
    List<Question> getAllQuestions();
    Question getQuestion(Long idQuestion);
    Question createQuestion(QuestionRequest questionRequest);
    Question updateQuestion(Long idQuestion, QuestionRequest questionRequest);
    void deleteQuestion(Long idQuestion);
}

