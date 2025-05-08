package com.example.productsimilarityservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
            // timeout de respuesta
            .responseTimeout(Duration.ofSeconds(3))
            // timeouts de lectura/escritura
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(3))
                    .addHandlerLast(new WriteTimeoutHandler(3)));

        return builder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}