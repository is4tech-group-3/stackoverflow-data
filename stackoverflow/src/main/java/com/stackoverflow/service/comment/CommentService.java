package com.stackoverflow.service.comment;

import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.dto.comment.CommentResponse;

import org.springframework.data.domain.Page;

public interface CommentService {
    CommentResponse createComment(Long idComment, CommentRequest commentRequest);
    Page<CommentResponse> getCommentsByPublicationId(Long idComment, int page, int size, String sortBy, String sortDirection);
    CommentResponse findCommentById(Long idComment);
    CommentResponse updateComment(Long idComment, CommentRequest commentRequest);
    void deleteComment(Long idComment);
}
