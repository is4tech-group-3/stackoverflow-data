package com.stackoverflow.service.answerLike;

public interface AnswerLikeService {
    void giveLike(Long idAnswer);

    void removeLike(Long idAnswer);

    Long countLikes(Long idAnswer);
}
