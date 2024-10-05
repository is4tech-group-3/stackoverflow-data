package com.stackoverflow.dto.question;

import lombok.Data;

import java.util.Set;

@Data
public class QuestionRequest {
    private String title;
    private String description;
    private Set<Long> idTags;
}

