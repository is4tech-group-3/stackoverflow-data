package com.stackoverflow.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Size(max = 1024, message = "The description must not be longer than 1024 characters")
    @NotNull(message = "The description field cannot be null")
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_update")
    private LocalDateTime dateUpdate;

    @Column(name = "publication_id")
    private Long idPublication;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
