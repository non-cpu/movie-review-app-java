package com.example.moviereviewappjava.dto;

import com.example.moviereviewappjava.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private long movieId;
    private double rating;
    private String content;

    public Review toEntity() {
        return Review.builder()
                .id(id)
                .movieId(movieId)
                .rating(rating)
                .content(content)
                .build();
    }
}