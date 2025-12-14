package com.dnk.iam;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.dnk.iam.infrastructure.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class IamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IamServiceApplication.class, args);
	}

	// ---- DIAGNOSTIC DB CONNECTION (TEMPORAL) ----
    @org.springframework.context.annotation.Bean
    CommandLineRunner checkDatabaseConnection(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("✅ [" + connection.getSchema() + "] Database connection SUCCESS");
            } catch (Exception e) {
                System.err.println("❌ Database connection FAILED");
                e.printStackTrace();
            }
        };
    }

}
