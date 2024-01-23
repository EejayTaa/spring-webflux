package com.reactivespring.exception;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({WebExchangeBindException.class})
  public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException webExchangeBindException){
    log.error("Exception: {} ", webExchangeBindException.getMessage(), webExchangeBindException);
    var error = webExchangeBindException.getBindingResult().getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .sorted()
        .collect(Collectors.joining(","));

    log.error("Error message: {}", error);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

}
