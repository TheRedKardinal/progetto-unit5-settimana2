package com.example.demo.service;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void inviaEmailHtml(String destinatario, String oggetto, String corpoHtml) {
        MimeMessage messaggio = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(messaggio, StandardCharsets.UTF_8.name());
            helper.setTo(destinatario);
            helper.setSubject(oggetto);
            helper.setText(corpoHtml, true);
        } catch (MessagingException e) {
            throw new IllegalStateException("Errore nella creazione dell'email", e);
        }

        mailSender.send(messaggio);
    }
}
