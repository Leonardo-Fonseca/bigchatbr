package br.com.leofonseca.bigchatbr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BigchatbrApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigchatbrApplication.class, args);
	}

}
