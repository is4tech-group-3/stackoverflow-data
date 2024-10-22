package com.stackoverflow.service.publication;

import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import org.springframework.data.domain.Page;

public interface PublicationService {
    PublicationResponse createPublication(PublicationRequest publicationRequest);

    Page<PublicationResponse> getPublications(int page, int size, String sortby, String sortDirection);

    PublicationResponse findPublicationById(Long idPublication);

    PublicationResponse updatePublication(Long idPublication, PublicationRequest publicationRequest);

    void deletePublication(Long idPublication);

    Page<PublicationResponse> getPublicationsByTag(int page, int size, String sortBy, String sortDirection, Long idTag);
}
