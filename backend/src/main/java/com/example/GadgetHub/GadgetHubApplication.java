package com.example.GadgetHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // 👈 Add this import

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }) // 👈 Exclude DataSource auto config
public class GadgetHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(GadgetHubApplication.class, args);
	}
}

