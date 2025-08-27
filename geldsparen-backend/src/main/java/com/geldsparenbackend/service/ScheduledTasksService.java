package com.geldsparenbackend.service;

import com.geldsparenbackend.model.MonthlyPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledTasksService {
    private final MonthlyPaymentService monthlyPaymentService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Autowired
    public ScheduledTasksService(MonthlyPaymentService monthlyPaymentService,
                                 NotificationService notificationService,
                                 EmailService emailService) {
        this.monthlyPaymentService = monthlyPaymentService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    /**
     * مهمة مجدولة للتحقق من الدفعات المتأخرة
     */
    @Scheduled(cron = "0 0 8 * * ?") // تشغيل كل يوم في الساعة 8 صباحاً
    public void checkForOverduePayments() {
        LocalDate today = LocalDate.now();
        List<MonthlyPayment> overduePayments = monthlyPaymentService.getOverduePayments();

        for (MonthlyPayment payment : overduePayments) {
            if (payment.getStatus() != MonthlyPayment.PaymentStatus.OVERDUE) {
                // هذا الجزء يحتاج إلى معالجة في MonthlyPaymentService
                monthlyPaymentService.markPaymentAsOverdue(payment.getId());

                // إرسال إشعار
                notificationService.createNotification(
                        payment.getUser().getId(),
                        "دفعة متأخرة ⚠️",
                        String.format("دفعة بقيمة %.2f € لهدف '%s' متأخرة. يرجى السداد فوراً.",
                                payment.getAmount(), payment.getSavingGoal().getName()),
                        "payment_overdue"
                );

                // إرسال بريد إلكتروني
                if (emailService != null) {
                    emailService.sendOverduePaymentEmail(
                            payment.getUser().getEmail(),
                            payment.getSavingGoal().getName(),
                            payment.getAmount(),
                            payment.getDueDate()
                    );
                }
            }
        }
    }

    /**
     * مهمة مجدولة للتحقق من الدفعات المستحقة قريباً
     */
    @Scheduled(cron = "0 0 9 * * ?") // تشغيل كل يوم في الساعة 9 صباحاً
    public void checkForUpcomingPayments() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<MonthlyPayment> duePayments = monthlyPaymentService.getPaymentsDueTomorrow(tomorrow);

        for (MonthlyPayment payment : duePayments) {
            if (payment.getStatus() == MonthlyPayment.PaymentStatus.PENDING) {
                notificationService.createNotification(
                        payment.getUser().getId(),
                        "تذكير بالدفعة 📅",
                        String.format("لديك دفعة مستحقة غداً بقيمة %.2f € لهدف '%s'",
                                payment.getAmount(), payment.getSavingGoal().getName()),
                        "payment_reminder"
                );

                // إرسال بريد إلكتروني للتذكير
                if (emailService != null) {
                    emailService.sendPaymentReminderEmail(
                            payment.getUser().getEmail(),
                            payment.getSavingGoal().getName(),
                            payment.getAmount(),
                            payment.getDueDate()
                    );
                }
            }
        }
    }
}