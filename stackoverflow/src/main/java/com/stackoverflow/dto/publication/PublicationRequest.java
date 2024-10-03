package com.stackoverflow.dto.publication;

import lombok.Data;

import java.util.Set;

@Data
public class PublicationRequest {
    private String title;
    private String description;
    private Set<Long> idTags;
}
