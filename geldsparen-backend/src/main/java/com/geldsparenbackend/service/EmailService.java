package com.geldsparenbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendGroupInvitation(String toEmail, String groupName,
                                    String inviterName, String savingGoalName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Einladung zur Gruppe: " + groupName);
            message.setText("Hallo!\n\n" +
                    inviterName + " hat Sie zur Gruppe '" + groupName +
                    "' für das Sparziel '" + savingGoalName + "' eingeladen.\n\n" +
                    "Bitte loggen Sie sich ein, um die Einladung anzunehmen oder abzulehnen.");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + toEmail + ", error: " + e.getMessage());
        }
    }

}