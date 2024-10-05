package com.stackoverflow.service.comment;

import com.stackoverflow.bo.Comment;
import com.stackoverflow.bo.Publication;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.comment.CommentRequest;
import com.stackoverflow.repository.CommentRepository;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getComment(Long idComment) {
        return commentRepository.findById(idComment)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    @Override
    public Comment createComment(Long idPublication, CommentRequest commentRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Publication publication = publicationRepository.findById(idPublication)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));

        Comment comment = Comment.builder()
                .description(commentRequest.getDescription())
                .dateCreation(LocalDateTime.now())
                .dateUpdate(LocalDateTime.now())
                .publication(publication)
                .user(user)
                .build();
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long idComment, CommentRequest commentRequest) {
        Optional<Comment> commentFound = commentRepository.findById(idComment);
        if (commentFound.isEmpty()) {
            throw new EntityNotFoundException("Commento not found");
        }

        Comment comment = commentFound.get();
        comment.setDescription(commentRequest.getDescription());
        comment.setDateUpdate(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long idComment) {
        commentRepository.findById(idComment)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentRepository.deleteById(idComment);
    }
}
