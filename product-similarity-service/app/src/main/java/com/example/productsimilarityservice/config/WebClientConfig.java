package com.example.productsimilarityservice.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // Reactor Netty con conexión pool y timeouts
        HttpClient httpClient = HttpClient.create(ConnectionProvider.builder("fixed")
                .maxConnections(50)
                .pendingAcquireTimeout(Duration.ofSeconds(5))
                .build())
            .responseTimeout(Duration.ofSeconds(3));

        return builder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}