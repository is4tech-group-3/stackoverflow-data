package com.stackoverflow.service.comment;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.dto.comment.CommentResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.CommentRepository;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

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
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final CommentRepository commentRepository;
    private final Validator validator;

    private static final String COMMENT_NOT_FOUND = "Comment not found with ID: ";
    private static final String USER_NOT_FOUND = "User not found with ID: ";
    private static final String PUBLICATION_NOT_FOUND = "Publication not found with ID: ";
    private static final String ADMIN = "ROLE_ADMIN";

    @Override
    public CommentResponse createComment(Long idPublication, CommentRequest commentRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + userId));
        if (!publicationRepository.existsById(idPublication)) {
            throw new EntityNotFoundException(PUBLICATION_NOT_FOUND + idPublication);
        }
        Comment comment = Comment.builder()
                .description(commentRequest.getDescription())
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .idPublication(idPublication)
                .user(user)
                .build();
        commentRepository.save(comment);

        return createCommentResponse(comment);
    }

    @Override
    public Page<CommentResponse> getCommentsByPublicationId(Long idPublication, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> commentPage = commentRepository.findByIdPublication(idPublication, pageable);

        return commentPage.map(this::createCommentResponse);
    }

    @Override
    public CommentResponse findCommentById(Long idComment) {
        Comment comment = commentRepository.findById(idComment)
                .orElseThrow(() -> new EntityNotFoundException(
                        COMMENT_NOT_FOUND + idComment));
        return createCommentResponse(comment);
    }

    @Override
    public CommentResponse updateComment(Long idComment, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(idComment)
                .orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND + idComment));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(comment.getUser().getId(), userId) && !roles.contains(ADMIN)) {
            throw new AccessDeniedException("You do not have permission to edit this comment");
        }

        comment.setDescription(commentRequest.getDescription());
        comment.setDateUpdate(LocalDateTime.now());

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        commentRepository.save(comment);

        return createCommentResponse(comment);
    }

    @Override
    public void deleteComment(Long idComment) {
        Comment comment = commentRepository.findById(idComment)
                .orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND + idComment));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long userId = ((User) userDetails).getId();
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        if (!Objects.equals(comment.getUser().getId(), userId) && !roles.contains(ADMIN)) {
            throw new AccessDeniedException("You do not have permission to delete this comment");
        }
        commentRepository.deleteById(idComment);
    }

    public CommentResponse createCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .idComment(comment.getId())
                .description(comment.getDescription())
                .dateCreated(comment.getDateCreation())
                .dateUpdated(comment.getDateUpdate())
                .idPublication(comment.getIdPublication())
                .author(
                        new UserResponse(
                                comment.getUser().getId(),
                                comment.getUser().getName(),
                                comment.getUser().getSurname(),
                                comment.getUser().getUsername(),
                                comment.getUser().getImage()
                        )
                )
                .build();
    }
}
