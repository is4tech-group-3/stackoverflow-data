package com.stackoverflow.controller;

import com.stackoverflow.service.answerLike.AnswerLikeService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/answer")
public class AnswerLikeController {
    private final AnswerLikeService answerLikeService;

    private final String ENTITY_NAME = "ANSWER";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping("/like/{id}")
    public void giveAnswerLike(@PathVariable("id") Long idAnswer) {
        answerLikeService.giveLike(idAnswer);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/dislike/{id}")
    public void giveAnswerDislike(@PathVariable("id") Long idAnswer) {
        answerLikeService.removeLike(idAnswer);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping("/likes/{id}")
    public ResponseEntity<Long> countLikes(@PathVariable("id") Long idAnswer) {
        Long likes = answerLikeService.countLikes(idAnswer);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }
}
