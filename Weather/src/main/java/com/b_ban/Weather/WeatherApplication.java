package com.b_ban.Weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WeatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

	// 기상청, 에어코리아 등 외부 REST API 호출용 HTTP 클라이언트
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	// JSON 문자열과 자바 객체 사이를 변환하기 위한 Jackson 도구
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
