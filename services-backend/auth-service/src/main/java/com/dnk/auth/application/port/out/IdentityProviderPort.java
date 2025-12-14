package com.dnk.auth.application.port.out;

import com.dnk.auth.application.model.VerifiedIdentity;

public interface IdentityProviderPort {

    VerifiedIdentity verifyIdToken(String idToken);
}
