package com.reactivespring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.reactivespring.domain.MovieInfo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

//look for repository classes and make these classes available upon test
//So you don't need to run the entire context application just to test the repository
@DataMongoTest
@ActiveProfiles("test")
class IMovieInfoRepositoryIntgTest {

  @Autowired
  IMovieInfoRepository movieInfoRepository;

  @BeforeEach //execute before test run
  void setUp(){
    var movieInfos = List.of(
        new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
        new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
        new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

    movieInfoRepository
        .saveAll(movieInfos)
        .blockLast(); // make sure that this call finish before test cases will be called. only allowed in test
  }

  @AfterEach
  void tearDown() {
    movieInfoRepository
        .deleteAll()
        .block();
  }
  @Test
  void findAll(){
    //given

    //when
    var moviesInfoFlux = movieInfoRepository
        .findAll()
        .log();

    //then
    StepVerifier.create(moviesInfoFlux)
        .expectNextCount(3)
        .verifyComplete();

  }

  @Test
  void findById(){
    String movieId = "abc";
    var movieMono = movieInfoRepository.findById(movieId).log();
    StepVerifier.create(movieMono)
        .assertNext(movieInfo -> {
          assertEquals(movieInfo.getName(), "Dark Knight Rises");
        })
        .verifyComplete();
  }

  @Test
  void saveMovieInfo(){
    MovieInfo movieInfo = new MovieInfo(null, "Batman Begins3", 2008, List.of("Christian Bale2", "Michael Cane1"), LocalDate.parse("2005-06-15"));
    var movieSaveMono = movieInfoRepository.save(movieInfo).log();
    StepVerifier.create(movieSaveMono)
        .assertNext(info -> {
          assertNotNull(info.getMovieInfoId());
          assertEquals(info.getName(), "Batman Begins3");
        })
        .verifyComplete();
  }

  @Test
  void updateMovieInfo(){

    String movieId = "abc";
    var movieInfo = movieInfoRepository.findById(movieId).block();
    movieInfo.setYear(2021);

    var movieSaveMono = movieInfoRepository.save(movieInfo).log();
    StepVerifier.create(movieSaveMono)
        .assertNext(info -> {
          assertNotNull(info.getMovieInfoId());
          assertEquals(info.getYear(), 2021);
        })
        .verifyComplete();
  }

  @Test
  void deleteMovieInfo(){

    var movieId = "abc";
    movieInfoRepository.deleteById(movieId).block();
    var movieInfoFlux = movieInfoRepository.findAll().log();

    StepVerifier.create(movieInfoFlux)
        .expectNextCount(2 )
        .verifyComplete();
  }

  @Test
  void  findByYear(){
    //given
    Integer year = 2005;
    //when
    var moviesInfoFlux = movieInfoRepository
        .findByYear(year)
        .log();

    //then
    StepVerifier
        .create(moviesInfoFlux)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void findByName(){
    String name = "Batman Begins";

    var findName = movieInfoRepository.findByName(name).log();

    StepVerifier.create(findName)
        .expectNextCount(1)
        .verifyComplete();
}

}