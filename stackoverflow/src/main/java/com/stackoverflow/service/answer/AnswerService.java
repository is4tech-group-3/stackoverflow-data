package com.stackoverflow.service.answer;

import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;

import java.util.List;

public interface AnswerService {
    AnswerResponse createAnswer(Long idQuestion, AnswerRequest answerRequest);

    List<AnswerResponse> getAnswersByQuestionId(Long idQuestion);

    AnswerResponse findAnswerById(Long idAnswer);

    AnswerResponse updateAnswer(Long idAnswer, AnswerRequest answerRequest);

    void deleteAnswer(Long idAnswer);
}
