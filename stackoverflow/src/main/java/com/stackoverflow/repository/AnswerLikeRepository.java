package com.stackoverflow.repository;

import com.stackoverflow.bo.AnswerLike;
import com.stackoverflow.bo.AnswerLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerLikeRepository extends JpaRepository<AnswerLike, AnswerLikeId> {
    Long countByIdAnswerId(long answerId);
}
