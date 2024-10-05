package com.stackoverflow.repository;

import com.stackoverflow.bo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
