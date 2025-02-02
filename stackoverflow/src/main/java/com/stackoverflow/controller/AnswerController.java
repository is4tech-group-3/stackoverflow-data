package com.stackoverflow.controller;

import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;
import com.stackoverflow.service.answer.AnswerService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/answer")
public class AnswerController {

    private final AnswerService answerService;

    private static final String ENTITY_NAME = "ANSWER";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping("/{idQuestion}")
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("idQuestion") Long idQuestion,
                                                       @RequestBody AnswerRequest answerRequest) {
        AnswerResponse answer = answerService.createAnswer(idQuestion, answerRequest);
        return new ResponseEntity<>(answer, HttpStatus.CREATED);
    }

    @GetMapping("/{idQuestion}")
    public ResponseEntity<Page<AnswerResponse>> getAllAnswers(
            @PathVariable("idQuestion") Long idQuestion,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "dateCreated") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection) {

        Page<AnswerResponse> answers = answerService.getAnswersByQuestionId(idQuestion, page, size, sortBy, sortDirection);
        return new ResponseEntity<>(answers, HttpStatus.OK);
    }

    @GetMapping("/findById/{idAnswer}")
    public ResponseEntity<AnswerResponse> findAnswerById(@PathVariable("idAnswer") Long idAnswer) {
        AnswerResponse answer = answerService.findAnswerById(idAnswer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{idAnswer}")
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable("idAnswer") Long idAnswer,
                                                       @RequestBody AnswerRequest answerRequest) {
        AnswerResponse answer = answerService.updateAnswer(idAnswer, answerRequest);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("{idAnswer}")
    public ResponseEntity<String> deleteAnswer(@PathVariable("idAnswer") Long idAnswer) {
        answerService.deleteAnswer(idAnswer);
        return new ResponseEntity<>("Answer deleted successfully", HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PatchMapping("/verified/{idQuestion}/{idAnswer}")
    public void verifyAnswer(@PathVariable("idQuestion") Long idQuestion, @PathVariable("idAnswer") Long idAnswer) {
        answerService.verifiedAnswer(idQuestion, idAnswer);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PatchMapping("/unverified/{idQuestion}/{idAnswer}")
    public void removeVerifiedAnswer(@PathVariable("idQuestion") Long idQuestion, @PathVariable("idAnswer") Long idAnswer) {
        answerService.removeVerifiedAnswer(idQuestion, idAnswer);
    }

    @GetMapping("/verifiedByQuestion/{idQuestion}")
    public ResponseEntity<AnswerResponse> getVerifiedByQuestion(@PathVariable("idQuestion") Long idQuestion) {
        AnswerResponse answer = answerService.getAnswerVerifiedByQuestionId(idQuestion);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
