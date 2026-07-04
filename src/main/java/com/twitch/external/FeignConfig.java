package com.twitch.external;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    // Defaults to your curl key/host if not found in application.properties
    @Value("${rapidapi.gutenberg.key:7870b184ddmshe4e7f33b2d8f770p1f5641jsn1ad7b20a44ee}")
    private String apiKey;

    @Value("${rapidapi.gutenberg.host:project-gutenberg-free-books-api1.p.rapidapi.com}")
    private String apiHost;

    @Bean
    public RequestInterceptor rapidApiInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Rapidapi-Key", apiKey);
            requestTemplate.header("X-Rapidapi-Host", apiHost);
        };
    }
}
