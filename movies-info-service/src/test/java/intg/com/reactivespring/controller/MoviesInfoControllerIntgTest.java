package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.IMovieInfoRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient //we need the webclienttest
class MoviesInfoControllerIntgTest {

  @Autowired
  IMovieInfoRepository movieInfoRepository;

  @Autowired
  WebTestClient webTestClient;

  static String MOVIES_INFO_URL = "/v1/movieInfos";

  @BeforeEach
  void setUp() {

    var movieInfos = List.of(
        new MovieInfo("1", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
            LocalDate.parse("2005-06-15")),
        new MovieInfo("2", "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
            LocalDate.parse("2008-07-18")),
        new MovieInfo("3", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
            LocalDate.parse("2012-07-20")));

    movieInfoRepository
        .saveAll(movieInfos)
        .blockLast(); // make sure that this call finish before test cases will be called. only allowed in test
  }

  @AfterEach
  void tearDown() {
    movieInfoRepository.deleteAll().block();
  }

  @Test
  void addMovieInfo() {
    //given
    var movieInfo = new MovieInfo(null, "Batman Begins", 2005,
        List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

    //when

    webTestClient.post()
        .uri(MOVIES_INFO_URL)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
          assert savedMovieInfo != null;
          assert savedMovieInfo.getMovieInfoId() != null;
        });
  }

  @Test
  void getAllMovieInfos() {
    webTestClient
        .get()
        .uri(MOVIES_INFO_URL)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBodyList(MovieInfo.class)
        .hasSize(3);
  }
  @Test
  void getAllMovieByYear() {
    var URI = UriComponentsBuilder
        .fromUriString(MOVIES_INFO_URL)
        .queryParam("year", 2005)
            .buildAndExpand().toUri();
    webTestClient
        .get()
        .uri(URI)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBodyList(MovieInfo.class)
        .hasSize(1);
  }

  @Test
  void getMovieInfoById() {
    String movieId = "1";
    var movie = new MovieInfo("1", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
        LocalDate.parse("2005-06-15"));
/**
 * Way # 1
 */
//    webTestClient
//        .get()
//        .uri(MOVIES_INFO_URL.concat("/").concat(movieId))
//        .exchange()
//        .expectStatus()
//        .is2xxSuccessful()
//        .expectBody(MovieInfo.class)
//        .consumeWith(movieInfoEntityExchangeResult -> {
//          var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
//          assertEquals(movieInfo, movie);
//        });

    /**
     * Way # 2
     */
    webTestClient
        .get()
        .uri(MOVIES_INFO_URL.concat("/").concat(movieId))
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Batman Begins");

  }

  @Test
  void getMovieInfoById_notFound() {
    String movieId = "12";
    var movie = new MovieInfo("1", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
        LocalDate.parse("2005-06-15"));

    webTestClient
        .get()
        .uri(MOVIES_INFO_URL.concat("/").concat(movieId))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void updateMovieInfo() {
    String movieId = "1";
    var movie = new MovieInfo("1", "Batman Begins1", 2008,
        List.of("Christian Bale", "Michael Cane"),
        LocalDate.parse("2005-06-15"));
    webTestClient
        .put()
        .uri(MOVIES_INFO_URL.concat("/").concat(movieId))
        .bodyValue(movie)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Batman Begins1")
        .jsonPath("$.year").isEqualTo(2008);
  }
  @Test
  void updateMovieInfo_notFound() {
    String movieId = "12";
    var movie = new MovieInfo("1", "Batman Begins1", 2008,
        List.of("Christian Bale", "Michael Cane"),
        LocalDate.parse("2005-06-15"));
    webTestClient
        .put()
        .uri(MOVIES_INFO_URL.concat("/").concat(movieId))
        .bodyValue(movie)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void deleteMovieInfo(){
    String movieId = "1";
    webTestClient.delete()
        .uri(MOVIES_INFO_URL.concat("/")
            .concat(movieId))
        .exchange()
        .expectStatus()
        .is2xxSuccessful();

    webTestClient
        .get()
        .uri(MOVIES_INFO_URL)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBodyList(MovieInfo.class)
        .hasSize(2);
  }
}