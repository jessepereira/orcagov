package br.com.orcagov.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableJpaAuditing
public class ApiApplication {

	private static final Logger logger = LoggerFactory.getLogger(ApiApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
		logger.info("Aplicação iniciada com sucesso.");

	}

}
