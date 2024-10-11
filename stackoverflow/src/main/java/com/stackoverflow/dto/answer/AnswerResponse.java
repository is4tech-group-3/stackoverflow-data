package com.stackoverflow.dto.answer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stackoverflow.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AnswerResponse {
    private Long idAnswer;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateUpdated;
    private Integer likes;
    private Boolean verified;
    private Long idQuestion;
    private UserResponse author;
}
