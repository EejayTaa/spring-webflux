package com.reactivespring.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MoviesInfoController.class)
public class MoviesInfoControllerUnitTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private MovieInfoService movieInfoServiceMock;

  static String MOVIES_INFO_URL = "/v1/movieInfos";

  @Test
  public void givenMovieInfos_WhenGetAllMoviesInfoInvoked_ThenMockValueReturned() {
    var movieInfoList = Flux.just(
        new MovieInfo("1", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
            LocalDate.parse("2005-06-15")),
        new MovieInfo("2", "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
            LocalDate.parse("2008-07-18")),
        new MovieInfo("3", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
            LocalDate.parse("2012-07-20")));

    when(movieInfoServiceMock.getAllMovieInfos())
        .thenReturn(movieInfoList);

    webTestClient
        .get()
        .uri(MOVIES_INFO_URL)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(MovieInfo.class)
        .hasSize(3);
  }

  @Test
  public void givenMovieInfoId_WhenGetMovieInfoById_ThenReturnMockMovieInfoById(){
    var movie = new MovieInfo("1", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
        LocalDate.parse("2005-06-15"));
    String movieId = "1";

    when(movieInfoServiceMock.getMovieInfoById(movieId))
        .thenReturn(Mono.just(movie));

    webTestClient
        .get()
        .uri(MOVIES_INFO_URL.concat("/").concat(movieId))
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var body = movieInfoEntityExchangeResult.getResponseBody();
          assert body != null;
          assertEquals(body, movie);
        });
  }

  @Test
  public void givenMovieInfo_WhenAddMovieInvoked_ThenReturnMockMovieInfo(){
    var movieInfo = new MovieInfo("1", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
        LocalDate.parse("2005-06-15"));
    when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));


    webTestClient.post()
        .uri(MOVIES_INFO_URL)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var body = movieInfoEntityExchangeResult.getResponseBody();
          assert body != null;
          assert body.getName().equals("Batman Begins");
        });
  }

  @Test
  public void givenMovieInfoId_WhenDeleteMovieInfoInvoked_ThenMockDeleteMovieInfoById(){

    String movieId = "1";

    when(movieInfoServiceMock.deleteMovieInfo(movieId))
      .thenReturn(Mono.empty());

    webTestClient
        .delete()
        .uri(MOVIES_INFO_URL.concat("/").concat(movieId))
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .isEmpty();
  }

  @Test
  public void givenMovieInfo_WhenAddMovieInvoked_ThenReturnMockMovieInfo_Validation(){
    var movieInfo = new MovieInfo("1", "", -2005, List.of("", "Michael Cane"),
        LocalDate.parse("2005-06-15"));

    webTestClient.post()
        .uri(MOVIES_INFO_URL)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(String.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var body = movieInfoEntityExchangeResult.getResponseBody();
          System.out.println(body);
          assert body != null;
          assertEquals(body, "movieInfo.cast must be present,movieInfo.name must not be blank,movieInfo.year must be a Positive value");
        });
  }


}
