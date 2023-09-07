package com.example.moviereviewappjava.controller;

import com.example.moviereviewappjava.domain.Review;
import com.example.moviereviewappjava.dto.ReviewDTO;
import com.example.moviereviewappjava.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        Review savedReview = reviewService.createReview(reviewDTO.toEntity());
        return new ResponseEntity<>(savedReview.toDTO(), HttpStatus.CREATED);
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMovie(
            @PathVariable long movieId,
            @RequestParam(required = false) Double minRating) {
        List<ReviewDTO> reviews = reviewService.getReviewsByMovie(movieId, minRating);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}