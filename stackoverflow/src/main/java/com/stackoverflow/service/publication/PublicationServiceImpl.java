package com.stackoverflow.service.publication;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    public PublicationResponse createPublication(PublicationRequest publicationRequest) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(publicationRequest.getIdTags()));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();
        UserResponse userResponse = userRepository.findUserResponseById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Publication publication = Publication.builder()
                .title(publicationRequest.getTitle())
                .description(publicationRequest.getDescription())
                .dateCreation(LocalDateTime.now())
                .dateUpdated(LocalDateTime.now())
                .userId(userId)
                .tags(tags)
                .build();
        publicationRepository.save(publication);
        return createPublicationResponse(publication);
    }


   @Override
    public Page<PublicationResponse> getPublications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Publication> publications = publicationRepository.findAll(pageable);
        return publications.stream().map(this::createPublicationResponse).collect(Collectors.toList());
    }

    @Override
    public PublicationResponse findPublicationById(Long idPublication) {
        Publication publication = publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + idPublication));
        return createPublicationResponse(publication);
    }

    @Override
    public PublicationResponse updatePublication(Long idPublication, PublicationRequest publicationRequest) {
        Publication publication = publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + idPublication));
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(publicationRequest.getIdTags()));
        publication.setTitle(publicationRequest.getTitle());
        publication.setDescription(publicationRequest.getDescription());
        publication.setTags(tags);
        publication.setDateUpdated(LocalDateTime.now());
        publicationRepository.save(publication);
        return createPublicationResponse(publication);
    }

    @Override
    public void deletePublication(Long idPublication) {
        publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + idPublication));
        publicationRepository.deleteById(idPublication);
    }

    public PublicationResponse createPublicationResponse(Publication publication) {
        return PublicationResponse.builder()
                .idPublication(publication.getIdPublication())
                .title(publication.getTitle())
                .description(publication.getDescription())
                .dateCreation(publication.getDateCreation())
                .dateUpdated(publication.getDateUpdated())
                .author(
                        userRepository.findUserResponseById(publication.getUserId())
                                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + publication.getUserId()))
                )
                .tags(publication.getTags())
                .build();
    }
}
