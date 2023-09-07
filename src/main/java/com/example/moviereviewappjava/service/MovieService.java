package com.example.moviereviewappjava.service;

import com.example.moviereviewappjava.domain.Genre;
import com.example.moviereviewappjava.domain.Movie;
import com.example.moviereviewappjava.domain.Review;
import com.example.moviereviewappjava.dto.MovieWithAvgRatingDTO;
import com.example.moviereviewappjava.repository.MovieRepository;
import com.example.moviereviewappjava.repository.ReviewRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie getMovieById(long movieId) {
        return movieRepository.findByIdAndDeletedIsFalse(movieId).orElseThrow(() ->
                new NoSuchElementException("Movie with ID " + movieId + " not found"));
    }

    public Page<MovieWithAvgRatingDTO> getMovies(Genre genre, Boolean isShowing, Pageable pageable) {
        Specification<Movie> specification = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[3];
            int index = 0;

            if (genre != null) {
                predicates[index++] = criteriaBuilder.equal(root.get("genre"), genre);
            }

            if (isShowing != null) {
                predicates[index++] = criteriaBuilder.equal(root.get("isShowing"), isShowing);
            }

            predicates[index] = criteriaBuilder.equal(root.get("deleted"), false);

            return criteriaBuilder.and(predicates);
        };

        Page<Movie> moviesPage = movieRepository.findAll(specification, pageable);

        return moviesPage.map(movie -> {
            Iterable<Review> reviews = reviewRepository.findAllByMovieId(movie.getId());
            Double avgRating = null;
            if (reviews.iterator().hasNext()) {
                avgRating = StreamSupport.stream(reviews.spliterator(), false)
                        .mapToDouble(Review::getRating)
                        .average().getAsDouble();
            }
            return new MovieWithAvgRatingDTO(movie.toDTO(), avgRating);
        });
    }

    public Movie updateMovie(long movieId, Movie updatedMovie) {
        Movie existingMovie = movieRepository.findByIdAndDeletedIsFalse(movieId).orElseThrow(() ->
                new NoSuchElementException("Movie with ID " + movieId + " not found"));

        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setGenre(updatedMovie.getGenre());
        existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
        existingMovie.setEndDate(updatedMovie.getEndDate());
        existingMovie.setShowing(updatedMovie.isShowing());

        return movieRepository.save(existingMovie);
    }

    public void softDeleteMovie(long movieId) {
        Movie movie = movieRepository.findByIdAndDeletedIsFalse(movieId).orElseThrow(() ->
                new NoSuchElementException("Movie with ID " + movieId + " not found"));
        movie.setDeleted(true);
        movieRepository.save(movie);
    }

    public void hardDeleteMovie(long movieId) {
        movieRepository.deleteById(movieId);
    }
}