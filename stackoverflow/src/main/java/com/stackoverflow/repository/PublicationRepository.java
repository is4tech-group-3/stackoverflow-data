package com.stackoverflow.repository;

import com.stackoverflow.bo.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findByTagsIdTag(Long idTag, Pageable pageable);
}
