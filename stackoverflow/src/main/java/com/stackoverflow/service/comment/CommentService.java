package com.stackoverflow.service.comment;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.dto.comment.CommentRequest;

import org.springframework.data.domain.Page;

public interface CommentService {
    Page<Comment> getComments(int page, int size);
    Comment getComment(Long idComment);
    Comment createComment(Long idPublication, CommentRequest commentRequest);
    Comment updateComment(Long idComment, CommentRequest commentRequest);
    void deleteComment(Long idComment);
}
