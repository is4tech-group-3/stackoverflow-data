package com.stackoverflow.controller;

import com.stackoverflow.dto.question.QuestionRequest;
import com.stackoverflow.dto.question.QuestionResponse;
import com.stackoverflow.service.question.QuestionService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/question")
public class QuestionController {

    private final QuestionService questionService;

    private static final String ENTITY_NAME = "QUESTION";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest questionRequest) {
        QuestionResponse question = questionService.createQuestion(questionRequest);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<QuestionResponse> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return questionService.getAllQuestions(page, size, sortBy, sortDirection);
    }

    @GetMapping("/{idQuestion}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable(name = "idQuestion") Long idQuestion) {
        QuestionResponse question = questionService.getQuestion(idQuestion);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @PutMapping("/{idQuestion}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable(name = "idQuestion") Long idQuestion,
            @RequestBody QuestionRequest questionRequest) {
        QuestionResponse question = questionService.updateQuestion(idQuestion, questionRequest);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{idQuestion}")
    public ResponseEntity<String> deleteQuestion(@PathVariable(name = "idQuestion") Long idQuestion) {
        questionService.deleteQuestion(idQuestion);
        return new ResponseEntity<>("Question deleted successfully", HttpStatus.OK);
    }
}
