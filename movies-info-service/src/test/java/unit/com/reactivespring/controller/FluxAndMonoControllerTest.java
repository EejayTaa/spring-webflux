package com.reactivespring.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void flux() {

    webTestClient
        .get()
        .uri("/flux")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Integer.class)
        .hasSize(5);
  }
  @Test
  void flux_approach2() {

    var result = webTestClient
        .get()
        .uri("/flux")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .returnResult(Integer.class)
        .getResponseBody();

    StepVerifier.create(result)
        .expectNext(1, 2, 3, 4, 5)
        .verifyComplete();
  }

  @Test
  void flux_approach3() {

    var result = webTestClient
        .get()
        .uri("/flux")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Integer.class)
            .consumeWith(listEntityExchangeResult -> {
              var responseBody = listEntityExchangeResult.getResponseBody();
              assert (Objects.requireNonNull(responseBody).size() == 5);
            });
  }

  @Test
  void mono() {
    var result = webTestClient
        .get()
        .uri("/mono")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(String.class)
        .consumeWith(stringEntityExchangeResult -> {
          var responseBody = stringEntityExchangeResult.getResponseBody();
          assertEquals(responseBody, "Hello world");
        });

  }

  @Test
  void stream() {

    var result = webTestClient
        .get()
        .uri("/stream")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .returnResult(Long.class)
        .getResponseBody();

    StepVerifier.create(result)
        .expectNext(0L,1L, 2L, 3L)
        .thenCancel()
        .verify();
  }
}