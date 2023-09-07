package com.example.moviereviewappjava.dto;

import com.example.moviereviewappjava.domain.Genre;
import com.example.moviereviewappjava.domain.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDTO {
    private Long id;
    private String title;
    private Genre genre;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private boolean isShowing;

    public Movie toEntity() {
        return Movie.builder()
                .id(id)
                .title(title)
                .genre(genre)
                .releaseDate(releaseDate)
                .endDate(endDate)
                .isShowing(isShowing)
                .build();
    }
}