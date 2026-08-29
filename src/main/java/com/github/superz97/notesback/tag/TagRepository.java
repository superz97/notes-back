package com.github.superz97.notesback.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByUserId(Long userId);
    Optional<Tag> findByUserIdAndName(Long userId, String name);

}
