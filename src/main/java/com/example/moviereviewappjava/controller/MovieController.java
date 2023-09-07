package com.example.moviereviewappjava.controller;

import com.example.moviereviewappjava.domain.Genre;
import com.example.moviereviewappjava.domain.Movie;
import com.example.moviereviewappjava.dto.MovieDTO;
import com.example.moviereviewappjava.dto.MovieWithAvgRatingDTO;
import com.example.moviereviewappjava.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody MovieDTO movieDTO) {
        if (movieDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be null or empty");
        }

        Movie savedMovie = movieService.createMovie(movieDTO.toEntity());
        return new ResponseEntity<>(savedMovie.toDTO(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable long id) {
        Movie movie = movieService.getMovieById(id);
        return new ResponseEntity<>(movie.toDTO(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<MovieWithAvgRatingDTO>> getMovies(
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Boolean isShowing,
            @PageableDefault(size = 2, direction = Sort.Direction.DESC, sort = "releaseDate") Pageable pageable) {
        Page<MovieWithAvgRatingDTO> movies = movieService.getMovies(genre, isShowing, pageable);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable long id, @RequestBody Movie updatedMovie) {
        Movie movie = movieService.updateMovie(id, updatedMovie);
        return new ResponseEntity<>(movie.toDTO(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable long id) {
        movieService.softDeleteMovie(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}