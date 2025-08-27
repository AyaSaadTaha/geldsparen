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

    @Async
    public void sendPaymentConfirmationEmail(String toEmail, String goalName, BigDecimal amount, LocalDateTime paidAt) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("تم تأكيد سداد الدفعة - Geldsparen");
        message.setText(String.format(
                "عزيزي المستخدم،\n\n" +
                        "تم سداد دفعة بنجاح لهدف التوفير '%s'.\n" +
                        "المبلغ: %.2f €\n" +
                        "وقت السداد: %s\n\n" +
                        "شكراً لاستخدامك تطبيق Geldsparen!\n\n" +
                        "مع أطيب التمنيات،\nفريق Geldsparen",
                goalName, amount, paidAt.toString()
        ));

        mailSender.send(message);
    }

    @Async
    public void sendPaymentReminderEmail(String toEmail, String goalName, BigDecimal amount, LocalDate dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("تذكير بالدفعة المستحقة - Geldsparen");
        message.setText(String.format(
                "عزيزي المستخدم،\n\n" +
                        "هذا تذكير بدفعة مستحقة غداً:\n" +
                        "الهدف: %s\n" +
                        "المبلغ: %.2f €\n" +
                        "تاريخ الاستحقاق: %s\n\n" +
                        "يرجى确保 السداد في الوقت المحدد.\n\n" +
                        "مع أطيب التمنيات،\nفريق Geldsparen",
                goalName, amount, dueDate.toString()
        ));

        mailSender.send(message);
    }

    @Async
    public void sendOverduePaymentEmail(String toEmail, String goalName, BigDecimal amount, LocalDate dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("تنبيه: دفعة متأخرة - Geldsparen");
        message.setText(String.format(
                "عزيزي المستخدم،\n\n" +
                        "لديك دفعة متأخرة تحتاج إلى السداد:\n" +
                        "الهدف: %s\n" +
                        "المبلغ: %.2f €\n" +
                        "تاريخ الاستحقاق: %s\n\n" +
                        "يرجى السداد في أقرب وقت ممكن.\n\n" +
                        "مع أطيب التمنيات،\nفريق Geldsparen",
                goalName, amount, dueDate.toString()
        ));

        mailSender.send(message);
    }

    @Async
    public void sendGroupInvitationEmail(String toEmail, String inviterName, String groupName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("دعوة للانضمام إلى مجموعة - Geldsparen");
        message.setText(String.format(
                "عزيزي المستخدم،\n\n" +
                        "تمت دعوتك للانضمام إلى مجموعة '%s' بواسطة %s.\n\n" +
                        "يرجى تسجيل الدخول إلى تطبيق Geldsparen للرد على الدعوة.\n\n" +
                        "مع أطيب التمنيات،\nفريق Geldsparen",
                groupName, inviterName
        ));

        mailSender.send(message);
    }
}