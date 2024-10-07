package com.stackoverflow.service.publication;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final TagRepository tagRepository;

    @Override
    public List<Publication> getPublications() {
        return publicationRepository.findAll();
    }

    @Override
    public Publication getPublication(Long idPublication) {
        return publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + idPublication));
    }

    @Override
    public Publication createPublication(PublicationRequest publicationRequest) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(publicationRequest.getIdTags()));
        Publication publication = Publication.builder()
                .title(publicationRequest.getTitle())
                .description(publicationRequest.getDescription())
                .dateCreation(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .tags(tags)
                .build();
        return publicationRepository.save(publication);
    }

    @Override
    public Publication updatePublication(Long idPublication, PublicationRequest publicationRequest) {
        Publication publication = publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + idPublication));
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(publicationRequest.getIdTags()));
        publication.setTitle(publicationRequest.getTitle());
        publication.setDescription(publicationRequest.getDescription());
        publication.setTags(tags);
        publication.setDateUpdated(LocalDateTime.now());
        return publicationRepository.save(publication);
    }

    @Override
    public void deletePublication(Long idPublication) {
        publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + idPublication));
        publicationRepository.deleteById(idPublication);
    }
}
