package com.system.cleanspring.domain.member;

public interface PasswordEncoder {
    String encode(String password);
    boolean matches(String password, String passwordHash);
}
