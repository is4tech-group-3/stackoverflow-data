package com.stackoverflow.service.publication;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.bo.Tag;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.util.ValidationUtil;

import com.stackoverflow.util.LoggerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final Validator validator;

    @Override
    public PublicationResponse createPublication(PublicationRequest publicationRequest) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(publicationRequest.getIdTags()));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        userRepository.findUserResponseById(userId)
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
    public Page<PublicationResponse> getPublications(int page, int size, String sortBy, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending(); 
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Publication> publications = publicationRepository.findAll(pageable);
        return publications.map(this::createPublicationResponse);
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

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        if (!Objects.equals(publication.getUserId(), userId) && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("You do not have permission to edit this publication");
        }

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(publicationRequest.getIdTags()));
        publication.setTitle(publicationRequest.getTitle());
        publication.setDescription(publicationRequest.getDescription());
        publication.setTags(tags);
        publication.setDateUpdated(LocalDateTime.now());

        Set<ConstraintViolation<Publication>> violations = validator.validate(publication);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        publicationRepository.save(publication);

        return createPublicationResponse(publication);
    }

    @Override
    public void deletePublication(Long idPublication) {
        Publication publication = publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + idPublication));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(publication.getUserId(), userId) && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("You do not have permission to edit this answer");
        }
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
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "User not found with id: " + publication.getUserId())))
                .tags(publication.getTags())
                .build();
    }
}
