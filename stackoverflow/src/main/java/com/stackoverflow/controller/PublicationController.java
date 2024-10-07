package com.stackoverflow.controller;

import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import com.stackoverflow.service.publication.PublicationService;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/publication")
public class PublicationController {

    private final PublicationService publicationService;

    @PostMapping
    public ResponseEntity<PublicationResponse> createPublication(@RequestBody PublicationRequest publicationRequest) {
        PublicationResponse publication = publicationService.createPublication(publicationRequest);
        return new ResponseEntity<>(publication, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<PublicationResponse> getPublications(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return publicationService.getPublications(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationResponse> getPublicationById(@PathVariable("id") Long id) {
        PublicationResponse publication = publicationService.findPublicationById(id);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublicationResponse> updatePublication(@PathVariable("id") Long id, @RequestBody PublicationRequest publicationRequest) {
        PublicationResponse publication = publicationService.updatePublication(id, publicationRequest);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable("id") Long id) {
        publicationService.deletePublication(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
    }
}
