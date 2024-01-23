package com.reactivespring.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class MovieInfo {

  @Id
  private String movieInfoId;
  @NotBlank(message = "movieInfo.name must not be blank")
  private String name;
  @NotNull
  @Positive(message = "movieInfo.year must be a Positive value")
  private Integer year; //the movie year released

  private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;
  private LocalDate releaseDate;
}
