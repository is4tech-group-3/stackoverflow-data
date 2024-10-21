package com.stackoverflow.controller;

import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.dto.comment.CommentResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testCreateComment() {
        Long idPublication = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setDescription("This is a comment");

        CommentResponse commentResponse = CommentResponse.builder()
                .idComment(1L)
                .description("This is a comment")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .idPublication(idPublication)
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .build();

        when(commentService.createComment(idPublication, commentRequest)).thenReturn(commentResponse);

        ResponseEntity<CommentResponse> response = commentController.createComment(idPublication, commentRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(commentResponse, response.getBody());
    }

    @Test
    void testGetCommentsByPublicationId() {
        Long idPublication = 1L;
        CommentResponse commentResponse = CommentResponse.builder()
                .idComment(1L)
                .description("This is a comment")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .idPublication(idPublication)
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .build();

        Page<CommentResponse> commentPage = new PageImpl<>(List.of(commentResponse));

        when(commentService.getCommentsByPublicationId(idPublication, 0, 10, "dateCreation", "desc"))
                .thenReturn(commentPage);

        ResponseEntity<Page<CommentResponse>> response = commentController.getComments(idPublication, 0, 10, "dateCreation", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(commentResponse, response.getBody().getContent().get(0));
    }

    @Test
    void testFindCommentById() {
        Long idComment = 1L;
        CommentResponse commentResponse = CommentResponse.builder()
                .idComment(idComment)
                .description("This is a comment")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .idPublication(1L)
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .build();

        when(commentService.findCommentById(idComment)).thenReturn(commentResponse);

        ResponseEntity<CommentResponse> response = commentController.getComment(idComment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentResponse, response.getBody());
    }

    @Test
    void testUpdateComment() {
        Long idComment = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setDescription("Updated comment");

        CommentResponse commentResponse = CommentResponse.builder()
                .idComment(idComment)
                .description("Updated comment")
                .dateCreated(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .idPublication(1L)
                .author(new UserResponse(1L, "Juan", "Herrera", "jherrera7$", "image.png"))
                .build();

        when(commentService.updateComment(idComment, commentRequest)).thenReturn(commentResponse);

        ResponseEntity<CommentResponse> response = commentController.updateComment(idComment, commentRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentResponse, response.getBody());
    }

    @Test
    void testDeleteComment() {
        Long idComment = 1L;

        doNothing().when(commentService).deleteComment(idComment);

        ResponseEntity<String> response = commentController.deleteComment(idComment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully", response.getBody());
    }
}
