package com.geldsparenbackend.service;

import com.geldsparenbackend.model.Notification;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.model.GroupMember.InvitationStatus;
import com.geldsparenbackend.repository.NotificationRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // إنشاء إشعار جديد
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // إنشاء إشعار مع بيانات أساسية
    public Notification createNotification(User user, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    // الحصول على جميع إشعارات المستخدم
    public List<Notification> getUserNotifications(String username) {
        User user = userService.findByUsername(username);
        return notificationRepository.findByUser(user);
    }

    // الحصول على الإشعارات غير المقروءة للمستخدم
    public List<Notification> getUnreadNotifications(String username) {
        User user = userService.findByUsername(username);
        return notificationRepository.findByUserAndIsRead(user, false);
    }

    // الحصول على عدد الإشعارات غير المقروءة
    public Long getUnreadCount(String username) {
        User user = userService.findByUsername(username);
        return notificationRepository.countUnreadByUser(user);
    }

    // تحديد الإشعار كمقروء
    public void markAsRead(Long notificationId, String username) {
        User user = userService.findByUsername(username);
        notificationRepository.markAsRead(notificationId, user);
    }

    // تحديد جميع الإشعارات كمقروءة
    public void markAllAsRead(String username) {
        User user = userService.findByUsername(username);
        notificationRepository.markAllAsReadByUser(user);
    }

    // حذف إشعار
    public void deleteNotification(Long notificationId, String username) {
        User user = userService.findByUsername(username);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // التحقق من أن الإشعار ينتمي للمستخدم
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    // إرسال إشعار بدعوة مجموعة
    public void sendGroupInvitation(User invitedUser, Group group, User inviter) {
        String title = "دعوة مجموعة";
        String message = String.format("تمت دعوتك للانضمام إلى مجموعة '%s' بواسطة %s",
                group.getName(), inviter.getUsername());
        String type = "GROUP_INVITATION";

        createNotification(invitedUser, title, message, type);
    }

    // إرسال إشعار برد على دعوة مجموعة
    public void sendGroupInvitationResponse(GroupMember groupMember, InvitationStatus response) {
        String title = "رد على دعوة المجموعة";
        String message = String.format("قام %s بـ %s دعوة الانضمام إلى مجموعة '%s'",
                groupMember.getUser().getUsername(),
                response == InvitationStatus.ACCEPTED ? "قبول" : "رفض",
                groupMember.getGroup().getName());
        String type = "GROUP_INVITATION_RESPONSE";

        createNotification(groupMember.getGroup().getCreatedBy(), title, message, type);
    }

    // إرسال إشعار بدفع مستحق
    public void sendPaymentReminder(User user, String savingGoalName, LocalDate dueDate) {
        String title = "تذكير بالدفع";
        String message = String.format("لديك دفعة مستحقة لهدف التوفير '%s' في %s",
                savingGoalName, dueDate.toString());
        String type = "PAYMENT_REMINDER";

        createNotification(user, title, message, type);
    }

    // إرسال إشعار بتحقيق هدف التوفير
    public void sendGoalAchievedNotification(User user, String savingGoalName) {
        String title = "تهانينا!";
        String message = String.format("لقد حققت هدف التوفير '%s' بنجاح!", savingGoalName);
        String type = "GOAL_ACHIEVED";

        createNotification(user, title, message, type);
    }

    // إرسال إشعار بدفعة تمت معالجتها
    public void sendPaymentProcessedNotification(User user, String savingGoalName, BigDecimal amount) {
        String title = "تمت معالجة الدفعة";
        String message = String.format("تمت معالجة دفعة بقيمة %s لهدف التوفير '%s'",
                amount.toString(), savingGoalName);
        String type = "PAYMENT_PROCESSED";

        createNotification(user, title, message, type);
    }

    // إرسال إشعار بإنذار رصيد منخفض
    public void sendLowBalanceAlert(User user, BigDecimal currentBalance, BigDecimal threshold) {
        String title = "إنذار رصيد منخفض";
        String message = String.format("رصيدك الحالي %s أقل من الحد الأدنى المحدد %s",
                currentBalance.toString(), threshold.toString());
        String type = "LOW_BALANCE_ALERT";

        createNotification(user, title, message, type);
    }

    // تنظيف الإشعارات القديمة
    public void cleanOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Notification> oldNotifications = notificationRepository.findExpiredNotifications(cutoffDate);

        if (!oldNotifications.isEmpty()) {
            notificationRepository.deleteAll(oldNotifications);
        }
    }

    // الحصول على الإشعارات بواسطة النوع
    public List<Notification> getNotificationsByType(String username, String type) {
        User user = userService.findByUsername(username);
        return notificationRepository.findByUserAndType(user, type);
    }
}