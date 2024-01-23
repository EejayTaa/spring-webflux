package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IMovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

  //return a movies based on year
  //findBy-
  Flux<MovieInfo> findByYear(Integer year);

  Flux<MovieInfo> findByName(String name);
}
