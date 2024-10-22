package com.stackoverflow.repository;

import com.stackoverflow.bo.Answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findByIdQuestion(Long idQuestion, Pageable pageable);
    Optional<Answer> findByIdAnswerAndIdQuestion(Long idAnswer, Long idQuestion);
    Optional<Answer> findByIdQuestionAndVerifiedTrue(Long idQuestion);
    Optional<Answer> findByIdQuestionAndIdAnswerAndVerifiedTrue(Long idQuestion, Long idAnswer);
}
