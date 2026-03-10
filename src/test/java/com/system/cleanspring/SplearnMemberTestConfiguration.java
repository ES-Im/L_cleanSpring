package com.system.cleanspring;

import com.system.cleanspring.application.member.required.EmailSender;
import com.system.cleanspring.domain.member.PasswordEncoder;
import com.system.cleanspring.domain.member.MemberFixture;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SplearnMemberTestConfiguration {
    @Bean
    public EmailSender emailSender() {
        return (email, subject, body) -> System.out.println("Sending email: " + email);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return MemberFixture.createPasswordEncoder();
    }
}
