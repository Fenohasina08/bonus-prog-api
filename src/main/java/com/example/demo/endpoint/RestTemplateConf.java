package com.example.demo.endpoint;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConf {

  @Bean
  public RestTemplate restTemplate(
      RestTemplateBuilder builder,
      @Value("${google.books.api.connect-timeout-ms}") long connectTimeoutMs,
      @Value("${google.books.api.read-timeout-ms}") long readTimeoutMs) {
    return builder
        .connectTimeout(Duration.ofMillis(connectTimeoutMs))
        .readTimeout(Duration.ofMillis(readTimeoutMs))
        .build();
  }
}
