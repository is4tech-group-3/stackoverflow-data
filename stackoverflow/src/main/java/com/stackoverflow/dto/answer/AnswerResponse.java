package com.stackoverflow.dto.answer;

import com.stackoverflow.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AnswerResponse {
    private String description;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private Integer likes;
    private Boolean verified;
    private Long idQuestion;
    private UserResponse author;
}
