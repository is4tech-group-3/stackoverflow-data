package com.stackoverflow.controller;

import com.stackoverflow.bo.Question;
import com.stackoverflow.dto.question.QuestionRequest;
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

    private final String ENTITY_NAME = "QUESTION";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody QuestionRequest questionRequest) {
        Question question = questionService.createQuestion(questionRequest);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<Question> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return questionService.getAllQuestions(page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable(name = "id") Long id) {
        Question question = questionService.getQuestion(id);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable(name = "id") Long id,
            @RequestBody QuestionRequest questionRequest) {
        Question question = questionService.updateQuestion(id, questionRequest);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable(name = "id") Long id) {
        questionService.deleteQuestion(id);
        return new ResponseEntity<>("Question deleted successfully", HttpStatus.OK);
    }
}
