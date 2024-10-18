package com.stackoverflow.controller;

import com.stackoverflow.service.answerlike.AnswerLikeService;
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

    private static final String ENTITY_NAME = "ANSWER";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping("/like/{idAnswer}")
    public void giveAnswerLike(@PathVariable("idAnswer") Long idAnswer) {
        answerLikeService.giveLike(idAnswer);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/dislike/{idAnswer}")
    public void giveAnswerDislike(@PathVariable("idAnswer") Long idAnswer) {
        answerLikeService.removeLike(idAnswer);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping("/likes/{idAnswer}")
    public ResponseEntity<Long> countLikes(@PathVariable("idAnswer") Long idAnswer) {
        Long likes = answerLikeService.countLikes(idAnswer);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }
}
