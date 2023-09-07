package com.example.moviereviewappjava;

import com.example.moviereviewappjava.domain.Genre;
import com.example.moviereviewappjava.domain.Movie;
import com.example.moviereviewappjava.dto.MovieDTO;
import com.example.moviereviewappjava.repository.MovieRepository;
import com.example.moviereviewappjava.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieRepository.deleteAll();
    }

    private Movie createInitialMovie() {
        return movieRepository.save(
                new Movie(
                        "Initial Movie",
                        Genre.ACTION,
                        LocalDate.of(2023, 8, 1),
                        LocalDate.of(2023, 8, 31),
                        true
                )
        );
    }

    @Test
    public void testCreateMovie() throws Exception {
        MovieDTO movieDTO = new MovieDTO(
                null,
                "Test Movie",
                Genre.ACTION,
                LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 31),
                true
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(movieDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Movie savedMovie = movieRepository.findByTitle(movieDTO.getTitle()).get();

        assertNotNull(savedMovie);
        assertEquals(movieDTO.getTitle(), savedMovie.getTitle());
        assertEquals(movieDTO.getGenre(), savedMovie.getGenre());
        assertEquals(movieDTO.getReleaseDate(), savedMovie.getReleaseDate());
        assertEquals(movieDTO.getEndDate(), savedMovie.getEndDate());
        assertEquals(movieDTO.isShowing(), savedMovie.isShowing());

        Long movieId = savedMovie.getId();

        if (movieId != null) {
            movieService.hardDeleteMovie(movieId);
        }
    }

    @Test
    public void testGetMovieById() throws Exception {
        Movie savedInitialMovie = createInitialMovie();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/{id}", savedInitialMovie.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedInitialMovie.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(savedInitialMovie.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(savedInitialMovie.getGenre().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value(savedInitialMovie.getReleaseDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endDate").value(savedInitialMovie.getEndDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.showing").value(savedInitialMovie.isShowing()));

        Long movieId = savedInitialMovie.getId();

        if (movieId != null) {
            movieService.hardDeleteMovie(movieId);
        }

    }

    @Test
    public void testGetMovies() throws Exception {
        List<Movie> movies = List.of(
                new Movie("Movie 1", Genre.ROMANCE, LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 31), true),
                new Movie("Movie 2", Genre.THRILLER, LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 31), false),
                new Movie("Movie 3", Genre.ROMANCE, LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 31), false),
                new Movie("Movie 4", Genre.ROMANCE, LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 31), false),
                new Movie("Movie 5", Genre.ROMANCE, LocalDate.of(2025, 8, 1), LocalDate.of(2023, 8, 31), false),
                new Movie("Movie 6", Genre.ROMANCE, LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 31), false),
                new Movie("Movie 7", Genre.ROMANCE, LocalDate.of(2024, 8, 1), LocalDate.of(2023, 8, 31), false)
        );
        List<Movie> savedMovies = movieRepository.saveAll(movies);

        int pageSize = 2;

        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/movies")
                                    .param("page", "0")
                                    .param("size", Integer.toString(pageSize))
                                    .param("genre", "ROMANCE")
                                    .param("isShowing", "false")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(pageSize))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(3))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].movie.title").value("Movie 5"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].movie.title").value("Movie 7"));
        } finally {
            movieRepository.deleteAll(savedMovies);
        }
    }

    @Test
    public void testUpdateMovie() throws Exception {
        Movie savedInitialMovie = createInitialMovie();

        MovieDTO updatedMovieDTO = new MovieDTO(
                savedInitialMovie.getId(),
                "Updated Movie",
                Genre.COMEDY,
                LocalDate.of(2023, 9, 1),
                LocalDate.of(2023, 9, 30),
                false
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/movies/{id}", savedInitialMovie.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedMovieDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        Movie updatedMovie = movieRepository.findById(savedInitialMovie.getId()).orElse(null);
        assertNotNull(updatedMovie);
        assertEquals(updatedMovieDTO.getTitle(), updatedMovie.getTitle());
        assertEquals(updatedMovieDTO.getGenre(), updatedMovie.getGenre());
        assertEquals(updatedMovieDTO.getReleaseDate(), updatedMovie.getReleaseDate());
        assertEquals(updatedMovieDTO.getEndDate(), updatedMovie.getEndDate());
        assertEquals(updatedMovieDTO.isShowing(), updatedMovie.isShowing());

        assert savedInitialMovie.getUpdatedAt() != null && !savedInitialMovie.getUpdatedAt().equals(updatedMovie.getUpdatedAt()) :
                "updatedAt should have changed";

        Long movieId = savedInitialMovie.getId();

        if (movieId != null) {
            movieService.hardDeleteMovie(movieId);
        }
    }

    @Test
    public void testSoftDeleteMovie() throws Exception {
        Movie savedInitialMovie = createInitialMovie();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movies/{id}", savedInitialMovie.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Movie deletedMovie = movieRepository.findById(savedInitialMovie.getId()).get();
        assertTrue(deletedMovie.isDeleted());

        Long movieId = savedInitialMovie.getId();

        if (movieId != null) {
            movieService.hardDeleteMovie(movieId);
        }
    }
}