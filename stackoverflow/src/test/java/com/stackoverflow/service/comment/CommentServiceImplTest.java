package com.stackoverflow.service.comment;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.dto.comment.CommentResponse;
import com.stackoverflow.repository.CommentRepository;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CommentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = new User();
        user.setId(1L);

        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreateComment() {
        CommentRequest request = new CommentRequest();
        request.setDescription("Test Comment");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setDescription("Test Comment");
        comment.setDateCreation(LocalDateTime.now());
        comment.setUser(new User());

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(publicationRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse response = commentService.createComment(1L, request);

        assertNotNull(response);
        assertEquals("Test Comment", response.getDescription());
    }

    @Test
    public void testGetCommentsByPublicationId() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setDescription("Test Comment");
        comment.setDateCreation(LocalDateTime.now());
        comment.setUser(new User());

        Page<Comment> page = new PageImpl<>(Collections.singletonList(comment));

        when(commentRepository.findByIdPublication(anyLong(), any(Pageable.class))).thenReturn(page);

        Page<CommentResponse> result = commentService.getCommentsByPublicationId(1L, 0, 10, "dateCreation", "ASC");

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Comment", result.getContent().get(0).getDescription());
    }

    @Test
    public void testUpdateCommentAccessDenied() {
        Long id = 1L;
        CommentRequest request = new CommentRequest();
        request.setDescription("Updated Comment");

        Comment existingComment = new Comment();
        existingComment.setId(id);
        existingComment.setDescription("Test Comment");
        existingComment.setUser(new User());

        User differentUser = new User();
        differentUser.setId(2L);

        when(commentRepository.findById(id)).thenReturn(Optional.of(existingComment));
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(differentUser);

        assertThrows(AccessDeniedException.class, () -> commentService.updateComment(id, request));
    }

    @Test
    public void testDeleteCommentAccessDenied() {
        Long id = 1L;

        Comment comment = new Comment();
        comment.setId(id);
        comment.setUser(new User());

        User differentUser = new User();
        differentUser.setId(2L);

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(differentUser);

        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(id));
    }
}
