package com.stackoverflow.bo;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AnswerLikeId implements Serializable {
    private Long answerId;
    private Long userId;
}
