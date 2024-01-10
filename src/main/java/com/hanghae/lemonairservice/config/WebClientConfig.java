package com.hanghae.lemonairservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {

//    @Bean
//    public DefaultUriBuilderFactory defaultUriBuilderFactory() {
//        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
//        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
//        return factory;
//    }
//
//    @Bean
//    public WebClient.Builder webClientBuilder(DefaultUriBuilderFactory factory) {
//        return WebClient.builder().uriBuilderFactory(factory);
//    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient getWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
}
