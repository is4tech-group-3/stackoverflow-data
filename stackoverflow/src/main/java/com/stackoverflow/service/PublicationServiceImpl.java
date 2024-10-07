package com.stackoverflow.service;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final TagRepository tagRepository;

    @Override
    public Page<Publication> getPublications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return publicationRepository.findAll(pageable);
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
