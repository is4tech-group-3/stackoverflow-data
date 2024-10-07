package com.stackoverflow.service.publication;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.dto.publication.PublicationRequest;

import java.util.List;

public interface PublicationService {
    List<Publication> getPublications();

    Publication getPublication(Long idPublication);

    Publication createPublication(PublicationRequest publicationRequest);

    Publication updatePublication(Long idPublication, PublicationRequest publicationRequest);

    void deletePublication(Long idPublication);
}
