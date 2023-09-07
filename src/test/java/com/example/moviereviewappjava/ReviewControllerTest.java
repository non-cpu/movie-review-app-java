package com.example.moviereviewappjava;

import com.example.moviereviewappjava.domain.Review;
import com.example.moviereviewappjava.dto.ReviewDTO;
import com.example.moviereviewappjava.repository.ReviewRepository;
import com.example.moviereviewappjava.service.ReviewService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    @BeforeEach
    public void setUp() {
        reviewRepository.deleteAll();
    }

    @Test
    public void testCreateReview() throws Exception {
        ReviewDTO reviewDTO = new ReviewDTO(null, 1L, 3.5, "content");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reviewDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<Review> savedReviews = reviewRepository.findAllByMovieId(reviewDTO.getMovieId());

        assertNotNull(savedReviews);
        assertEquals(1, savedReviews.size());

        Review savedReview = savedReviews.get(0);

        assertNotNull(savedReview);
        assertEquals(reviewDTO.getMovieId(), savedReview.getMovieId());
        assertEquals(reviewDTO.getRating(), savedReview.getRating());
        assertEquals(reviewDTO.getContent(), savedReview.getContent());

        reviewService.hardDeleteReview(savedReview.getId());
    }

    @Test
    public void testGetReviewsByMovie() throws Exception {
        List<Review> reviews = List.of(
                new Review(1L, 3.5, "0"),
                new Review(1L, 3.8, "1"),
                new Review(1L, 3.8, "2"),
                new Review(1L, 3.9, "3")
        );
        List<Review> savedReviews = reviewRepository.saveAll(reviews);

        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/reviews/movies/{movieId}", 1L)
                                    .param("minRating", "3.8")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].rating").value(3.9))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].rating").value(3.8))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].content").value("2"));
        } finally {
            reviewRepository.deleteAll(savedReviews);
        }
    }
}