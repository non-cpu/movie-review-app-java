package com.example.moviereviewappjava.domain;

import com.example.moviereviewappjava.dto.ReviewDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long movieId;
    private double rating;
    private String content;

    @CreationTimestamp
    private Timestamp createdAt;
    @CreationTimestamp
    private Timestamp updatedAt;
    private boolean deleted;

    public Review(Long movieId, double rating, String content) {
        this.movieId = movieId;
        this.rating = Math.min(rating, 5.0);
        this.content = content;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = Timestamp.from(Instant.now());
    }

    public ReviewDTO toDTO() {
        return ReviewDTO.builder()
                .id(id)
                .movieId(movieId)
                .rating(rating)
                .content(content)
                .build();
    }
}