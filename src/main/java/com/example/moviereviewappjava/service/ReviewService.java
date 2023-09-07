package com.example.moviereviewappjava.service;

import com.example.moviereviewappjava.domain.Review;
import com.example.moviereviewappjava.dto.ReviewDTO;
import com.example.moviereviewappjava.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<ReviewDTO> getReviewsByMovie(long movieId, Double minRating) {
        List<Review> reviews;
        if (minRating != null) {
            reviews = reviewRepository.findAllByMovieIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(movieId, minRating);
        } else {
            reviews = reviewRepository.findAllByMovieIdOrderByCreatedAtDesc(movieId);
        }

        return reviews.stream().map(Review::toDTO).collect(Collectors.toList());
    }

    public void hardDeleteReview(long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}