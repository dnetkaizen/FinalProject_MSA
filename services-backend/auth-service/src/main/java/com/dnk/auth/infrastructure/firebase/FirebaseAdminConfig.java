package com.dnk.auth.infrastructure.firebase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    private static final String FIREBASE_SERVICE_ACCOUNT_JSON = "FIREBASE_SERVICE_ACCOUNT_JSON";

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 1. Try JSON content from Env Var (Render/Prod)
        String firebaseJson = System.getenv(FIREBASE_SERVICE_ACCOUNT_JSON);
        
        if (firebaseJson != null && !firebaseJson.isBlank()) {
             try (InputStream serviceAccount = new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8))) {
                return initializeFirebase(serviceAccount, "environment variable JSON");
             }
        }

        // 2. Try File Path from Env Var (Local Dev)
        String serviceAccountPathValue = System.getenv(FIREBASE_SERVICE_ACCOUNT_ENV);
        if (serviceAccountPathValue != null && !serviceAccountPathValue.isBlank()) {
            Path serviceAccountPath = Paths.get(serviceAccountPathValue);
            if (Files.exists(serviceAccountPath)) {
                 try (InputStream serviceAccount = Files.newInputStream(serviceAccountPath)) {
                    return initializeFirebase(serviceAccount, "file: " + serviceAccountPath.toAbsolutePath());
                 }
            }
        }
        
        throw new IllegalStateException("Firebase configuration not found. Set FIREBASE_SERVICE_ACCOUNT_JSON or FIREBASE_SERVICE_ACCOUNT_PATH.");
    }
    
    private FirebaseApp initializeFirebase(InputStream credentialsStream, String source) throws IOException {
         FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();
        
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized successfully from {}", source);
            return app;
        } else {
            return FirebaseApp.getInstance();
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
