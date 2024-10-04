package com.stackoverflow.controller;

import com.stackoverflow.bo.Question;
import com.stackoverflow.dto.question.QuestionRequest;
import com.stackoverflow.service.question.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody QuestionRequest questionRequest, HttpServletRequest request) {
        Question question = questionService.createQuestion(questionRequest);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Question>> getQuestions(HttpServletRequest request) {
        List<Question> questions = questionService.getAllQuestions();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        Question question = questionService.getQuestion(id);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable(name = "id") Long id, @RequestBody QuestionRequest questionRequest, HttpServletRequest request) {
        Question question = questionService.updateQuestion(id, questionRequest);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        questionService.deleteQuestion(id);
        return new ResponseEntity<>("Question deleted successfully", HttpStatus.NO_CONTENT);
    }
}
