package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

  private MovieInfoService moviesInfoService;

  public MoviesInfoController(MovieInfoService movieInfoService) {
    this.moviesInfoService = movieInfoService;
  }

  @PostMapping("/movieInfos")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
    return moviesInfoService.addMovieInfo(movieInfo).log();
  }

  @GetMapping("/movieInfos")
  public Flux<MovieInfo> getAllMovieInfos(
      @RequestParam(value = "year", required = false) Integer year) {
    if (year != null) {
      return moviesInfoService.getMovieByYear(year);
    }
    return moviesInfoService.getAllMovieInfos();
  }

  @GetMapping("/movieInfos/{movieId}")
  public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String movieId) {
    return moviesInfoService.getMovieInfoById(movieId)
        .map(ResponseEntity.ok()::body)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @PutMapping("/movieInfos/{movieId}")
  public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String movieId,
      @RequestBody @Valid MovieInfo movieInfo) {
    return moviesInfoService.updateMovieInfo(movieId, movieInfo)
        .map(ResponseEntity.ok()::body)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
        .log();
  }

  @DeleteMapping("/movieInfos/{movieId}")
  public Mono<Void> deleteMovieInfo(@PathVariable String movieId) {
    return moviesInfoService.deleteMovieInfo(movieId);
  }
}
