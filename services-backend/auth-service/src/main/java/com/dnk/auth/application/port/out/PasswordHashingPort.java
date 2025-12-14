package com.dnk.auth.application.port.out;

public interface PasswordHashingPort {

    String hash(String raw);

    boolean matches(String raw, String hash);
}
