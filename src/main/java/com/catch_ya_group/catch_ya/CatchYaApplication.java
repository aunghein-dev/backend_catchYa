package com.catch_ya_group.catch_ya;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.catch_ya_group.catch_ya.modal.entity")
@EnableJpaRepositories(basePackages = "com.catch_ya_group.catch_ya.repository")
@EnableRetry
@EnableScheduling
public class CatchYaApplication {

	public static void main(String[] args) {
        try {
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());

			});
		} catch (io.github.cdimascio.dotenv.DotenvException e) {
			System.err.println("Warning: .env file not found or could not be loaded. Relying on system environment variables or application properties. Error: " + e.getMessage());
		}
		SpringApplication.run(CatchYaApplication.class, args);
	}
}
