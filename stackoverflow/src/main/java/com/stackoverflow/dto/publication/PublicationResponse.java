package com.stackoverflow.dto.publication;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class PublicationResponse {
    private Long idPublication;
    private String title;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateCreation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateUpdated;
    private UserResponse author;
    private Set<Tag> tags;
}
