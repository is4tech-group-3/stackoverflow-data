package com.stackoverflow.dto.comment;

import java.time.LocalDateTime;

import com.stackoverflow.dto.user.UserResponse;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentResponse {
    private Long idComment;
    private String description;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private Long idPublication;
    private UserResponse author;
}
