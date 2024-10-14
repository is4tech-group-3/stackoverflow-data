package com.stackoverflow.controller;

import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import com.stackoverflow.service.publication.PublicationService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/publication")
public class PublicationController {

    private final PublicationService publicationService;

    private final String ENTITY_NAME = "PUBLICATION";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping
    public ResponseEntity<PublicationResponse> createPublication(@RequestBody PublicationRequest publicationRequest) {
        PublicationResponse publication = publicationService.createPublication(publicationRequest);
        return new ResponseEntity<>(publication, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PublicationResponse>> getPublications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Page<PublicationResponse> publications = publicationService.getPublications(page, size, sortBy, sortDirection);
        return new ResponseEntity<>(publications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationResponse> getPublicationById(@PathVariable("id") Long id) {
        PublicationResponse publication = publicationService.findPublicationById(id);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{id}")
    public ResponseEntity<PublicationResponse> updatePublication(@PathVariable("id") Long id,
            @RequestBody PublicationRequest publicationRequest) {
        PublicationResponse publication = publicationService.updatePublication(id, publicationRequest);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable("id") Long id) {
        publicationService.deletePublication(id);
        return new ResponseEntity<>("Publication deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/findByTag/{idTag}")
    public ResponseEntity<Page<PublicationResponse>> getPublicationsByTag(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "dateCreation") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @PathVariable("idTag") Long idTag) {
        Page<PublicationResponse> publications = publicationService.getPublicationsByTag(page,size, sortBy, sortDirection, idTag);
        return new ResponseEntity<>(publications, HttpStatus.OK);
    }
}
