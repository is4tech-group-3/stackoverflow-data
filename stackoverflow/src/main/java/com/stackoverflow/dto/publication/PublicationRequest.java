package com.stackoverflow.dto.publication;

import lombok.Data;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

@Data
public class PublicationRequest {
    private String title;
    private String description;
    private Set<Long> idTags;
    private MultipartFile image;
}
