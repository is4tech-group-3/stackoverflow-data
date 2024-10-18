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

@Builder
@Data
@Table(name = "answers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long idAnswer;

    @Size(max = 1024, message = "The description must not be longer than 1024 characters")
    @NotNull(message = "The description field cannot be null")
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "date_creation")
    private LocalDateTime dateCreated;

    @Column(name = "date_update")
    private LocalDateTime dateUpdated;

    private Boolean verified;

    @Column(name = "question_id")
    private Long idQuestion;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
