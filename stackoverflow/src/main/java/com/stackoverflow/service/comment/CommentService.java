package com.stackoverflow.service.comment;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.dto.comment.CommentRequest;

import java.util.List;

public interface CommentService {
    List<Comment> getComments();
    Comment getComment(Long idComment);
    Comment createComment(Long idPublication, CommentRequest commentRequest);
    Comment updateComment(Long idComment, CommentRequest commentRequest);
    void deleteComment(Long idComment);
}
