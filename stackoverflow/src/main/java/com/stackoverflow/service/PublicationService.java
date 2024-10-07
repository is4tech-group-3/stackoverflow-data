package com.stackoverflow.service;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.dto.publication.PublicationRequest;

import org.springframework.data.domain.Page;

public interface PublicationService {
    Page<Publication> getPublications(int page, int size);

    Publication getPublication(Long idPublication);

    Publication createPublication(PublicationRequest publicationRequest);

    Publication updatePublication(Long idPublication, PublicationRequest publicationRequest);

    void deletePublication(Long idPublication);
}
