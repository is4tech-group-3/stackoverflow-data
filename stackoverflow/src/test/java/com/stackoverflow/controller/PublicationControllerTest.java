package com.stackoverflow.controller;

import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import com.stackoverflow.service.publication.PublicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PublicationControllerTest {
    @Mock
    private PublicationService publicationService;

    @InjectMocks
    private PublicationController publicationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePublication() {
        PublicationRequest request = new PublicationRequest();
        request.setTitle("Test Title");
        request.setDescription("Test Description");

        PublicationResponse response = new PublicationResponse(
                1L,
                "Test Title",
                "Test Description",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                "http://example.com/image.jpg"
        );

        when(publicationService.createPublication(any(PublicationRequest.class))).thenReturn(response);

        ResponseEntity<PublicationResponse> result = publicationController.createPublication(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Test Title", result.getBody().getTitle());
    }

    @Test
    public void testGetPublications() {
        PublicationResponse publicationResponse = new PublicationResponse(
                1L,
                "Test Title",
                "Test Description",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                "http://example.com/image.jpg"
        );

        Page<PublicationResponse> page = new PageImpl<>(Collections.singletonList(publicationResponse));

        when(publicationService.getPublications(0, 10, "dateCreation", "desc")).thenReturn(page);

        ResponseEntity<Page<PublicationResponse>> result = publicationController.getPublications(0, 10, "dateCreation", "desc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    public void testGetPublicationById() {
        PublicationResponse response = new PublicationResponse(
                1L,
                "Test Title",
                "Test Description",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                "http://example.com/image.jpg"
        );

        when(publicationService.findPublicationById(1L)).thenReturn(response);

        ResponseEntity<PublicationResponse> result = publicationController.getPublicationById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Test Title", result.getBody().getTitle());
    }

    @Test
    public void testUpdatePublication() {
        Long id = 1L;
        PublicationRequest request = new PublicationRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        PublicationResponse response = new PublicationResponse(
                id,
                "Updated Title",
                "Updated Description",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                "http://example.com/image.jpg"
        );

        when(publicationService.updatePublication(eq(id), any(PublicationRequest.class))).thenReturn(response);

        ResponseEntity<PublicationResponse> result = publicationController.updatePublication(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Updated Title", result.getBody().getTitle());
    }

    @Test
    public void testDeletePublication() {
        Long id = 1L;

        doNothing().when(publicationService).deletePublication(id);

        ResponseEntity<String> result = publicationController.deletePublication(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Publication deleted successfully", result.getBody());
    }
}
