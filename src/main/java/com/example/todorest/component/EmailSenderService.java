package com.example.todorest.component;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSenderService {
    private final JavaMailSender javaMailSender;

    @Async
    public void setJavaMailSender(String toMail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("onehealthmedicalcenterobehealt@gmail.com");
        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
