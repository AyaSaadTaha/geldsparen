package com.geldsparenbackend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class EmailService {
    private final JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendGroupInvitation(String toEmail, String groupName,
                                    String inviterName, String savingGoalName) {
        try {
            System.out.println("Attempting to send email to: {}" + toEmail);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎯 Einladung zur Gruppe: " + groupName);

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .button { display: inline-block; padding: 10px 20px; background-color: #4CAF50; 
                                 color: white; text-decoration: none; border-radius: 5px; }
                        .footer { margin-top: 20px; padding: 10px; text-align: center; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Gruppeneinladung</h1>
                        </div>
                        <div class="content">
                            <h2>Hallo!</h2>
                            <p><strong>%s</strong> hat Sie zur Gruppe <strong>'%s'</strong> 
                            für das Sparziel <strong>'%s'</strong> eingeladen.</p>
                            <p>Bitte loggen Sie sich in der Geldsparen App ein, um die Einladung anzunehmen oder abzulehnen.</p>
                            <p>Das ist unser Link : http://localhost:5173/</p>
                            <br>
                            <p>Mit freundlichen Grüßen,<br>Ihr Geldsparen Team</p>
                        </div>
                        <div class="footer">
                            <p>© 2024 Geldsparen App. Alle Rechte vorbehalten.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(inviterName, groupName, savingGoalName);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email successfully sent to: {}" + toEmail);

        } catch (Exception e) {
            System.out.println("Failed to send email to: {}" +toEmail+ e);
        }
    }

}