package com.stackoverflow.controller;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.service.publication.PublicationService;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<Publication> createPublication(@RequestBody PublicationRequest publicationRequest) {
        Publication publication = publicationService.createPublication(publicationRequest);
        return new ResponseEntity<>(publication, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Publication>> getPublications() {
        List<Publication> publications = publicationService.getPublications();
        return new ResponseEntity<>(publications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable("id") Long id) {
        Publication publication = publicationService.getPublication(id);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publication> updatePublication(@PathVariable("id") Long id, @RequestBody PublicationRequest publicationRequest) {
        Publication publication = publicationService.updatePublication(id, publicationRequest);
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable("id") Long id) {
        publicationService.deletePublication(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
    }
}
