package com.stackoverflow.controller;

import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import com.stackoverflow.service.publication.PublicationService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/publication")
public class PublicationController {

    private final PublicationService publicationService;

    private final String ENTITY_NAME = "PUBLICATION";

    @AuditAnnotation(ENTITY_NAME)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PublicationResponse> createPublication(@ModelAttribute PublicationRequest publicationRequest) {
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

    @GetMapping("/{idPublication}")
    public ResponseEntity<PublicationResponse> getPublicationById(@PathVariable("idPublication") Long idPublication) {
        PublicationResponse publication = publicationService.findPublicationById(idPublication);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{idPublication}")
    public ResponseEntity<PublicationResponse> updatePublication(@PathVariable("idPublication") Long idPublication,
            @RequestBody PublicationRequest publicationRequest) {
        PublicationResponse publication = publicationService.updatePublication(idPublication, publicationRequest);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{idPublication}")
    public ResponseEntity<String> deletePublication(@PathVariable("idPublication") Long idPublication) {
        publicationService.deletePublication(idPublication);
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
