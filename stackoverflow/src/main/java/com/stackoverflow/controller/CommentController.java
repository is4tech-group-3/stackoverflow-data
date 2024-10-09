package com.stackoverflow.controller;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.dto.comment.CommentResponse;
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
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable("idPublication") Long idPublication,
            @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.createComment(idPublication, commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping("/{idPublication}")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable("idPublication") Long idPublication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "dateCreation") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection) {

        Page<CommentResponse> comments = commentService.getCommentsByPublicationId(idPublication, page, size, sortBy,
                sortDirection);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable("id") Long idComment) {
        CommentResponse comment = commentService.findCommentById(idComment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("id") Long id,
            @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.updateComment(id, commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.NO_CONTENT);
    }
}
