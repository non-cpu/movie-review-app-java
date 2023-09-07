package com.example.moviereviewappjava.domain;

import com.example.moviereviewappjava.dto.MovieDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String title;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private LocalDate releaseDate;
    private LocalDate endDate;
    private boolean isShowing;

    @CreationTimestamp
    private Timestamp createdAt;
    @CreationTimestamp
    private Timestamp updatedAt;
    private boolean deleted = false;

    public Movie(String title, Genre genre, LocalDate releaseDate, LocalDate endDate, boolean isShowing) {
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.endDate = endDate;
        this.isShowing = isShowing;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public MovieDTO toDTO() {
        return MovieDTO.builder()
                .id(id)
                .title(title)
                .genre(genre)
                .releaseDate(releaseDate)
                .endDate(endDate)
                .isShowing(isShowing)
                .build();
    }
}