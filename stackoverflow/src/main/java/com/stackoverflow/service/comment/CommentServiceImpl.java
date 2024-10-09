package com.stackoverflow.service.comment;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.dto.comment.CommentResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.CommentRepository;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.util.ValidationUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PublicationRepository publicationRepository;

        private final CommentRepository commentRepository;

        @Override
        public CommentResponse createComment(Long idPublication, CommentRequest commentRequest) {
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                Long userId = ((User) userDetails).getId();

                ValidationUtil.validateNotEmpty(commentRequest.getDescription(), "Description");
                ValidationUtil.validateMaxLength(commentRequest.getDescription(), 255, "Description");

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));

                publicationRepository.findById(idPublication)
                                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));

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
        public Page<CommentResponse> getCommentsByPublicationId(Long idPublication, int page, int size, String sortBy,
                        String sortDirection) {
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
                                                "Comment not found with id: " + idComment));
                return createCommentResponse(comment);
        }

        @Override
        public CommentResponse updateComment(Long idComment, CommentRequest commentRequest) {
                Comment comment = commentRepository.findById(idComment)
                                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

                ValidationUtil.validateNotEmpty(commentRequest.getDescription(), "Description");
                ValidationUtil.validateMaxLength(commentRequest.getDescription(), 255, "Description");

                comment.setDescription(commentRequest.getDescription());
                comment.setDateUpdate(LocalDateTime.now());
                commentRepository.save(comment);
                return createCommentResponse(comment);
        }

        @Override
        public void deleteComment(Long idComment) {
                commentRepository.findById(idComment)
                                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
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
                                                new UserResponse(comment.getUser().getId(), comment.getUser().getName(),
                                                                comment.getUser().getSurname(),
                                                                comment.getUser().getUsername()))
                                .build();
        }
}
