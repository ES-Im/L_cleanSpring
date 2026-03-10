package com.system.cleanspring.adapter.security;

import com.system.cleanspring.domain.member.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecurePasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    @Override
    public String encode(String password) {
        return Objects.requireNonNull(bCryptPasswordEncoder.encode(password));
    }

    @Override
    public boolean matches(String password, String passwordHash) {
        return bCryptPasswordEncoder.matches(password, passwordHash);
    }
}
