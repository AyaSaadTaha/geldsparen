package com.geldsparenbackend.service;

import com.geldsparenbackend.model.MonthlyPayment;
import com.geldsparenbackend.model.PaymentHistory;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.repository.MonthlyPaymentRepository;
import com.geldsparenbackend.repository.SavingGoalRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MonthlyPaymentService {
    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final UserRepository userRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Autowired
    public MonthlyPaymentService(MonthlyPaymentRepository monthlyPaymentRepository,
                                 SavingGoalRepository savingGoalRepository,
                                 UserRepository userRepository,
                                 PaymentHistoryService paymentHistoryService,
                                 NotificationService notificationService,
                                 EmailService emailService) {
        this.monthlyPaymentRepository = monthlyPaymentRepository;
        this.savingGoalRepository = savingGoalRepository;
        this.userRepository = userRepository;
        this.paymentHistoryService = paymentHistoryService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    /**
     * إنشاء دفعات شهرية لهدف توفير جديد
     */
    @Transactional
    public List<MonthlyPayment> createMonthlyPaymentsForSavingGoal(SavingGoal savingGoal) {
        long monthsBetween = ChronoUnit.MONTHS.between(
                LocalDate.now().withDayOfMonth(1),
                savingGoal.getDeadline().withDayOfMonth(1)
        );

        if (monthsBetween <= 0) {
            throw new RuntimeException("Deadline must be in the future");
        }

        // استخدام المبلغ الشهري المحسوب مسبقاً في SavingGoal
        BigDecimal monthlyAmount = savingGoal.getMonthlyAmount();

        if (monthlyAmount == null) {
            throw new RuntimeException("Monthly amount not calculated for saving goal");
        }

        // إنشاء دفعة لكل شهر
        List<MonthlyPayment> payments = IntStream.range(0, (int) monthsBetween)
                .mapToObj(i -> {
                    MonthlyPayment payment = new MonthlyPayment();
                    payment.setSavingGoal(savingGoal);
                    payment.setUser(savingGoal.getUser());
                    payment.setAmount(monthlyAmount);
                    payment.setDueDate(LocalDate.now().plusMonths(i + 1).withDayOfMonth(1));
                    payment.setStatus(MonthlyPayment.PaymentStatus.PENDING);
                    return payment;
                })
                .collect(Collectors.toList());

        return monthlyPaymentRepository.saveAll(payments);
    }
    /**
     * الحصول على جميع الدفعات لهدف توفير معين
     */
    public List<MonthlyPayment> getPaymentsBySavingGoalId(Long savingGoalId) {
        return monthlyPaymentRepository.findBySavingGoalId(savingGoalId);
    }

    /**
     * الحصول على الدفعات الخاصة بمستخدم معين
     */
    public List<MonthlyPayment> getPaymentsByUserId(Long userId) {
        return monthlyPaymentRepository.findByUserId(userId);
    }

    /**
     * الحصول على الدفعات في نطاق زمني معين
     */
    public List<MonthlyPayment> getPaymentsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return monthlyPaymentRepository.findByUserIdAndDueDateRange(userId, startDate, endDate);
    }

    /**
     * تسديد دفعة
     */
    @Transactional
    public MonthlyPayment markPaymentAsPaid(Long paymentId, Long userId) {
        MonthlyPayment payment = monthlyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (!payment.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to update this payment");
        }

        MonthlyPayment.PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(MonthlyPayment.PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        MonthlyPayment updatedPayment = monthlyPaymentRepository.save(payment);

        // تسجيل في التاريخ
        paymentHistoryService.recordPayment(
                payment,
                MonthlyPayment.PaymentStatus.PAID,
                PaymentHistory.ChangeReason.PAYMENT_MADE.name(),
                userId
        );

        // تحديث مبلغ الهدف التوفيري
        updateSavingGoalCurrentAmount(payment.getSavingGoal().getId(), payment.getAmount());

        // إرسال إشعار
        notificationService.createNotification(
                userId,
                "تم سداد الدفعة",
                String.format("تم سداد دفعة بقيمة %.2f € لهدف '%s'",
                        payment.getAmount(), payment.getSavingGoal().getName()),
                "payment_success"
        );

        // إرسال بريد إلكتروني للتأكيد
        if (emailService != null) {
            emailService.sendPaymentConfirmationEmail(
                    payment.getUser().getEmail(),
                    payment.getSavingGoal().getName(),
                    payment.getAmount(),
                    payment.getPaidAt()
            );
        }

        return updatedPayment;
    }

    /**
     * تحديث مبلغ الهدف التوفيري بعد السداد
     */
    @Transactional
    public void updateSavingGoalCurrentAmount(Long savingGoalId, BigDecimal amount) {
        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with id: " + savingGoalId));

        BigDecimal newAmount = savingGoal.getCurrentAmount().add(amount);
        savingGoal.setCurrentAmount(newAmount);

        // التحقق إذا تم تحقيق الهدف
        if (newAmount.compareTo(savingGoal.getTargetAmount()) >= 0) {
            savingGoal.setStatus(SavingGoal.SavingGoalStatus.COMPLETED);

            notificationService.createNotification(
                    savingGoal.getUser().getId(),
                    "تهانينا! 🎉",
                    String.format("لقد حققت هدفك التوفيري '%s' بنجاح!", savingGoal.getName()),
                    "goal_achieved"
            );
        }

        savingGoalRepository.save(savingGoal);
    }

    /**
     * إعادة جدولة دفعة
     */
    @Transactional
    public MonthlyPayment reschedulePayment(Long paymentId, LocalDate newDueDate, Long userId) {
        MonthlyPayment payment = monthlyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (!payment.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to reschedule this payment");
        }

        LocalDate oldDueDate = payment.getDueDate();
        payment.setDueDate(newDueDate);

        MonthlyPayment updatedPayment = monthlyPaymentRepository.save(payment);

        // تسجيل في التاريخ
        paymentHistoryService.recordPaymentStatusChange(
                payment,
                payment.getStatus(),
                payment.getStatus(),
                String.format("تم إعادة الجدولة من %s إلى %s",
                        oldDueDate.toString(), newDueDate.toString()),
                userId
        );

        notificationService.createNotification(
                userId,
                "تم إعادة جدولة الدفعة",
                String.format("تم إعادة جدولة دفعة لهدف '%s' إلى %s",
                        payment.getSavingGoal().getName(), newDueDate.toString()),
                "payment_rescheduled"
        );

        return updatedPayment;
    }

    /**
     * الحصول على الدفعات المتأخرة
     */
    public List<MonthlyPayment> getOverduePayments() {
        return monthlyPaymentRepository.findOverduePayments(LocalDate.now());
    }

    /**
     * الحصول على الدفعات المستحقة غداً
     */
    public List<MonthlyPayment> getPaymentsDueTomorrow(LocalDate tomorrow) {
        return monthlyPaymentRepository.findPaymentsDueTomorrow(tomorrow);
    }

    /**
     * الحصول على إجمالي المبلغ المستحق
     */
    public BigDecimal getTotalDueAmount(Long userId) {
        List<MonthlyPayment> pendingPayments = monthlyPaymentRepository.findByUserId(userId).stream()
                .filter(p -> p.getStatus() == MonthlyPayment.PaymentStatus.PENDING)
                .collect(Collectors.toList());

        return pendingPayments.stream()
                .map(MonthlyPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * الحصول على إحصاءات الدفعات
     */
    public PaymentStatistics getPaymentStatistics(Long userId) {
        List<MonthlyPayment> allPayments = monthlyPaymentRepository.findByUserId(userId);

        long totalPayments = allPayments.size();
        long paidPayments = allPayments.stream()
                .filter(p -> p.getStatus() == MonthlyPayment.PaymentStatus.PAID)
                .count();
        long pendingPayments = allPayments.stream()
                .filter(p -> p.getStatus() == MonthlyPayment.PaymentStatus.PENDING)
                .count();
        long overduePayments = allPayments.stream()
                .filter(p -> p.getStatus() == MonthlyPayment.PaymentStatus.OVERDUE)
                .count();

        BigDecimal totalPaidAmount = allPayments.stream()
                .filter(p -> p.getStatus() == MonthlyPayment.PaymentStatus.PAID)
                .map(MonthlyPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPendingAmount = allPayments.stream()
                .filter(p -> p.getStatus() == MonthlyPayment.PaymentStatus.PENDING)
                .map(MonthlyPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PaymentStatistics(
                totalPayments, paidPayments, pendingPayments, overduePayments,
                totalPaidAmount, totalPendingAmount
        );
    }

    // إضافة method جديدة
    @Transactional
    public void markPaymentAsOverdue(Long paymentId) {
        MonthlyPayment payment = monthlyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        MonthlyPayment.PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(MonthlyPayment.PaymentStatus.OVERDUE);
        monthlyPaymentRepository.save(payment);

        // تسجيل في التاريخ
        paymentHistoryService.recordPaymentStatusChange(
                payment,
                oldStatus,
                MonthlyPayment.PaymentStatus.OVERDUE,
                PaymentHistory.ChangeReason.PAYMENT_OVERDUE.name(),
                payment.getUser().getId()
        );
    }


    /**
     * حذف جميع الدفعات لهدف توفير معين
     */
    @Transactional
    public void deletePaymentsBySavingGoalId(Long savingGoalId) {
        List<MonthlyPayment> payments = monthlyPaymentRepository.findBySavingGoalId(savingGoalId);
        monthlyPaymentRepository.deleteAll(payments);
    }

    /**
     * تحديث مبلغ الدفعة
     */
    @Transactional
    public MonthlyPayment updatePaymentAmount(Long paymentId, BigDecimal newAmount, Long userId) {
        MonthlyPayment payment = monthlyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (!payment.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to update this payment");
        }

        BigDecimal oldAmount = payment.getAmount();
        payment.setAmount(newAmount);

        MonthlyPayment updatedPayment = monthlyPaymentRepository.save(payment);

        // تسجيل في التاريخ
        paymentHistoryService.recordPaymentStatusChange(
                payment,
                payment.getStatus(),
                payment.getStatus(),
                String.format("تم تعديل المبلغ من %.2f إلى %.2f", oldAmount, newAmount),
                userId
        );

        notificationService.createNotification(
                userId,
                "تم تعديل مبلغ الدفعة",
                String.format("تم تعديل مبلغ الدفعة لهدف '%s' من %.2f إلى %.2f",
                        payment.getSavingGoal().getName(), oldAmount, newAmount),
                "payment_updated"
        );

        return updatedPayment;
    }

    /**
     * كلاس لإحصاءات الدفعات
     */
    public static class PaymentStatistics {
        private final long totalPayments;
        private final long paidPayments;
        private final long pendingPayments;
        private final long overduePayments;
        private final BigDecimal totalPaidAmount;
        private final BigDecimal totalPendingAmount;

        public PaymentStatistics(long totalPayments, long paidPayments, long pendingPayments,
                                 long overduePayments, BigDecimal totalPaidAmount, BigDecimal totalPendingAmount) {
            this.totalPayments = totalPayments;
            this.paidPayments = paidPayments;
            this.pendingPayments = pendingPayments;
            this.overduePayments = overduePayments;
            this.totalPaidAmount = totalPaidAmount;
            this.totalPendingAmount = totalPendingAmount;
        }

        // Getters
        public long getTotalPayments() { return totalPayments; }
        public long getPaidPayments() { return paidPayments; }
        public long getPendingPayments() { return pendingPayments; }
        public long getOverduePayments() { return overduePayments; }
        public BigDecimal getTotalPaidAmount() { return totalPaidAmount; }
        public BigDecimal getTotalPendingAmount() { return totalPendingAmount; }

        public BigDecimal getCompletionPercentage() {
            if (totalPaidAmount.add(totalPendingAmount).compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return totalPaidAmount.multiply(BigDecimal.valueOf(100))
                    .divide(totalPaidAmount.add(totalPendingAmount), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}