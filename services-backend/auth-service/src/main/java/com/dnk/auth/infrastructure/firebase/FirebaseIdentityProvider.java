package com.dnk.auth.infrastructure.firebase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dnk.auth.application.exception.AuthException;
import com.dnk.auth.application.model.VerifiedIdentity;
import com.dnk.auth.application.port.out.IdentityProviderPort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Component
public class FirebaseIdentityProvider implements IdentityProviderPort {

    private static final Logger log = LoggerFactory.getLogger(FirebaseIdentityProvider.class);

    private final FirebaseAuth firebaseAuth;

    public FirebaseIdentityProvider(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public VerifiedIdentity verifyIdToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);

            String providerUserId = decodedToken.getUid();
            String email = decodedToken.getEmail();
            boolean emailVerified = decodedToken.isEmailVerified();

            return new VerifiedIdentity(providerUserId, email, emailVerified);
        } catch (FirebaseAuthException | IllegalArgumentException ex) {
            log.warn("Invalid Firebase ID token", ex);
            throw new AuthException("Invalid Firebase ID token", ex);
        }
    }
}
