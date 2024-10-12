package com.stackoverflow.service.answerLike;


import com.stackoverflow.bo.AnswerLike;
import com.stackoverflow.bo.AnswerLikeId;
import com.stackoverflow.bo.User;
import com.stackoverflow.repository.AnswerLikeRepository;
import com.stackoverflow.repository.AnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AnswerLikeServiceImpl implements AnswerLikeService {

    private final AnswerLikeRepository answerLikeRepository;
    private final AnswerRepository answerRepository;

    @Override
    public void giveLike(Long idAnswer) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();
        if(userId == null) throw new EntityNotFoundException("user not found to establish relationship");
        answerRepository.findById(idAnswer)
                .orElseThrow(() -> new EntityNotFoundException("answer not found to establish relationship"));
        AnswerLikeId id = AnswerLikeId.builder()
                .userId(userId)
                .answerId(idAnswer)
                .build();
        AnswerLike answerLike = AnswerLike.builder()
                .id(id)
                .build();
        answerLikeRepository.save(answerLike);
    }

    @Override
    public void removeLike(Long idAnswer) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        AnswerLikeId id = AnswerLikeId.builder()
                .userId(userId)
                .answerId(idAnswer)
                .build();

        answerLikeRepository.deleteById(id);
    }

    @Override
    public Long countLikes(Long idAnswer) {
        return answerLikeRepository.countByIdAnswerId(idAnswer);
    }
}
