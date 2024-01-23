package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.IMovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {


  private IMovieInfoRepository movieInfoRepository;

  public MovieInfoService(IMovieInfoRepository movieInfoRepository){
    this.movieInfoRepository = movieInfoRepository;
  }


  public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo){
    return movieInfoRepository.save(movieInfo);
  }


  public Flux<MovieInfo> getAllMovieInfos() {
    return movieInfoRepository.findAll().log();
  }

  public Mono<MovieInfo> getMovieInfoById(String movieId) {
    return movieInfoRepository.findById(movieId).log();
  }

  public Mono<MovieInfo> updateMovieInfo(String movieId, MovieInfo updateMovieInfo) {

   return movieInfoRepository.findById(movieId)
        .flatMap(movieInfo -> {
          movieInfo.setMovieInfoId(updateMovieInfo.getMovieInfoId());
          movieInfo.setYear(updateMovieInfo.getYear());
          movieInfo.setCast(updateMovieInfo.getCast());
          movieInfo.setReleaseDate(updateMovieInfo.getReleaseDate());
          movieInfo.setName(updateMovieInfo.getName());
          return movieInfoRepository.save(movieInfo);
        });
  }

  public Mono<Void> deleteMovieInfo(String movieId) {
    return movieInfoRepository
        .deleteById(movieId)
        .log();
  }

  public Flux<MovieInfo> getMovieByYear(Integer year) {
    return movieInfoRepository.findByYear(year).log();
  }
}
