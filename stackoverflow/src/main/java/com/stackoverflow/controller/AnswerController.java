package com.stackoverflow.controller;

import com.stackoverflow.dto.answer.AnswerRequest;
import com.stackoverflow.dto.answer.AnswerResponse;
import com.stackoverflow.service.answer.AnswerService;
import com.stackoverflow.util.LoggerService;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/answer")
public class AnswerController {

    private final AnswerService answerService;

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

    @GetMapping("/findById/{id}")
    public ResponseEntity<AnswerResponse> findAnswerById(@PathVariable("id") Long idAnswer) {
        AnswerResponse answer = answerService.findAnswerById(idAnswer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable("id") Long idAnswer,
            @RequestBody AnswerRequest answerRequest) {
        AnswerResponse answer = answerService.updateAnswer(idAnswer, answerRequest);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAnswer(@PathVariable("id") Long idAnswer) {
        answerService.deleteAnswer(idAnswer);
        return new ResponseEntity<>("Answer deleted successfully", HttpStatus.OK);
    }
}
