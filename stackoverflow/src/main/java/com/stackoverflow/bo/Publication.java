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
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@Table(name = "publications")
@NoArgsConstructor
@AllArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publication_id")
    private Long idPublication;

    @Size(max = 50, message = "The title must not be longer than 50 characters")
    @NotNull(message = "The title field cannot be null")
    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 256, message = "The description must not be longer than 256 characters")
    @NotNull(message = "The description field cannot be null")
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_update")
    private LocalDateTime dateUpdated;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "url_image")
    private String urls;

    @ManyToMany
    @JoinTable(
            name = "publication_tag",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
