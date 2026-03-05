package com.system.cleanspring.adapter.integration;

import com.system.cleanspring.application.required.EmailSender;
import com.system.cleanspring.domain.Email;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Component
public class DummyEmailSender implements EmailSender {

    @Override
    public void send(Email email, String subject, String body) {
        System.out.println("DummyEmailSender send email: " + email);
    }
}
