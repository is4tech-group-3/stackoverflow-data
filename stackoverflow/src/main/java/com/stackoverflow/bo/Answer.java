package com.stackoverflow.bo;

import jakarta.persistence.*;
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

    private String description;

    @Column(name = "date_creation")
    private LocalDateTime dateCreated;

    @Column(name = "date_update")
    private LocalDateTime dateUpdated;

    private Integer likes;

    private Boolean verified;

    @Column(name = "question_id")
    private Long idQuestion;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
