package br.com.orcagov.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication


@EnableJpaAuditing
@EntityScan("br.com.orcagov.api.entity")
@EnableJpaRepositories(basePackages = "br.com.orcagov.api.repository")

public class ApiApplication {

	private static final Logger logger = LoggerFactory.getLogger(ApiApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
		logger.info("Aplicação iniciada com sucesso.");

	}

}
