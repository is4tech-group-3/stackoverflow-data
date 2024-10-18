package com.stackoverflow.service.answerlike;

public interface AnswerLikeService {
    void giveLike(Long idAnswer);

    void removeLike(Long idAnswer);

    Long countLikes(Long idAnswer);
}
