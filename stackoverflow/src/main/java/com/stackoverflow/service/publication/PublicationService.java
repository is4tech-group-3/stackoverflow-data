package com.stackoverflow.service.publication;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;

import java.util.List;

public interface PublicationService {
    PublicationResponse createPublication(PublicationRequest publicationRequest);

    List<PublicationResponse> getPublications();

    PublicationResponse findPublicationById(Long idPublication);


    PublicationResponse updatePublication(Long idPublication, PublicationRequest publicationRequest);

    void deletePublication(Long idPublication);
}
