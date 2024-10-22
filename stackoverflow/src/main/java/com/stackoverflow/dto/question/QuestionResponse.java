package com.stackoverflow.dto.question;

import java.time.LocalDateTime;
import java.util.Set;

import com.stackoverflow.bo.Tag;
import com.stackoverflow.dto.user.UserResponse;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QuestionResponse {
    private Long idQuestion;
    private String title;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateUpdate;
    private UserResponse author;
    private Set<Tag> tags;
}
