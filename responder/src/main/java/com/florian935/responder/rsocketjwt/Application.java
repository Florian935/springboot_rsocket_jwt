package com.florian935.responder.rsocketjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		Hooks.onErrorDropped(error -> {});
		SpringApplication.run(Application.class, args);
	}

}
