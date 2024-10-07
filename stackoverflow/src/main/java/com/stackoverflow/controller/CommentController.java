package com.stackoverflow.controller;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.service.comment.CommentService;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{idPublication}")
    public ResponseEntity<Comment> createComment(
            @PathVariable("idPublication") Long idPublication,
            @RequestBody CommentRequest commentRequest) {
        Comment comment = commentService.createComment(idPublication, commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<Comment> getComments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return commentService.getComments(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable("id") Long id){
        Comment comment = commentService.getComment(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable("id") Long id, @RequestBody CommentRequest commentRequest){
        Comment comment = commentService.updateComment(id, commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable("id") Long id){
        commentService.deleteComment(id);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.NO_CONTENT);
    }
}
