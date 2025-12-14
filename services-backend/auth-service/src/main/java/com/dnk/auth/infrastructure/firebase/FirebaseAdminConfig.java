package com.dnk.auth.infrastructure.firebase;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseAdminConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAdminConfig.class);
    private static final String FIREBASE_SERVICE_ACCOUNT_ENV = "FIREBASE_SERVICE_ACCOUNT_PATH";

    @Bean
    public FirebaseApp firebaseApp() {
        String serviceAccountPathValue = System.getenv(FIREBASE_SERVICE_ACCOUNT_ENV);
        if (serviceAccountPathValue == null || serviceAccountPathValue.isBlank()) {
            throw new IllegalStateException(
                    FIREBASE_SERVICE_ACCOUNT_ENV + " environment variable is not set or is blank");
        }

        Path serviceAccountPath = Paths.get(serviceAccountPathValue);
        if (!Files.exists(serviceAccountPath)) {
            throw new IllegalStateException(
                    "Firebase service account file not found at path: " + serviceAccountPath.toAbsolutePath());
        }

        try (InputStream serviceAccountStream = Files.newInputStream(serviceAccountPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp app;
            if (FirebaseApp.getApps().isEmpty()) {
                app = FirebaseApp.initializeApp(options);
            } else {
                app = FirebaseApp.getInstance();
            }

            log.info("FirebaseApp initialized successfully using service account file at: {}",
                    serviceAccountPath.toAbsolutePath());
            return app;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize FirebaseApp from service account file at path: "
                    + serviceAccountPath.toAbsolutePath(), e);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
