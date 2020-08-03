package io.jzheaux.springsecurity.resolutions;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class UserService {

	private final WebClient webClient;

	public UserService(WebClient.Builder webClient) {
		this.webClient = webClient.build();
	}

	public Optional<String> getFullName(String username) {
		String fullName = this.webClient.get()
				.uri("/user/{username}/fullName", username)
				.retrieve()
				.bodyToMono(String.class)
				.block();

		return Optional.ofNullable(fullName);
	}
}
