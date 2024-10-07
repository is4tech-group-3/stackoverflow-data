package com.stackoverflow.service.publication;

import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PublicationService {
    PublicationResponse createPublication(PublicationRequest publicationRequest);

    Page<PublicationResponse> getPublications(int page, int size);

    PublicationResponse findPublicationById(Long idPublication);

    PublicationResponse updatePublication(Long idPublication, PublicationRequest publicationRequest);

    void deletePublication(Long idPublication);
}
