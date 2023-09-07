package com.example.moviereviewappjava.repository;

import com.example.moviereviewappjava.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMovieId(long movieId);

    List<Review> findAllByMovieIdOrderByCreatedAtDesc(long movieId);

    List<Review> findAllByMovieIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(long movieId, double rating);
}