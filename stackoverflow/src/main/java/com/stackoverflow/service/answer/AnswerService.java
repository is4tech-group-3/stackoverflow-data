package com.stackoverflow.service.answer;

import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;

import java.util.List;

import org.springframework.data.domain.Page;

public interface AnswerService {
    AnswerResponse createAnswer(Long idQuestion, AnswerRequest answerRequest);

    Page<AnswerResponse> getAnswersByQuestionId(Long idQuestion, int page, int size);

    AnswerResponse findAnswerById(Long idAnswer);

    AnswerResponse updateAnswer(Long idAnswer, AnswerRequest answerRequest);

    void deleteAnswer(Long idAnswer);
}
