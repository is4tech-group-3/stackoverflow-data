package com.stackoverflow.controller;

import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.dto.comment.CommentResponse;
import com.stackoverflow.service.comment.CommentService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    private final String ENTITY_NAME = "COMMENT";

    @AuditAnnotation(ENTITY_NAME)
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

    @GetMapping("/findById/{idComment}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable("idComment") Long idComment) {
        CommentResponse comment = commentService.findCommentById(idComment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{idComment}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("idComment") Long idComment,
            @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.updateComment(idComment, commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{idComment}")
    public ResponseEntity<String> deleteComment(@PathVariable("idComment") Long idComment) {
        commentService.deleteComment(idComment);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }
}
