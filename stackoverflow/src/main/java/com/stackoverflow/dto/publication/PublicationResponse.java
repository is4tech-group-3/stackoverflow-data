package com.stackoverflow.dto.publication;

import com.stackoverflow.bo.Tag;
import com.stackoverflow.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class PublicationResponse {
    private String title;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateUpdated;
    private UserResponse author;
    private Set<Tag> tags;
}
