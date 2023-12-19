package com.hanghae.lemonairservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfiguration {
	@Bean
	public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
		return new LettuceConnectionFactory("localhost", 6379);
	}
}