package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.Notification;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // البحث عن الإشعارات بواسطة المستخدم
    List<Notification> findByUser(User user);

    // البحث عن الإشعارات بواسطة المستخدم وحالة القراءة
    List<Notification> findByUserAndIsRead(User user, Boolean isRead);

    // البحث عن الإشعارات الحديثة (بعد تاريخ معين)
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.createdAt >= :date")
    List<Notification> findRecentByUser(@Param("user") User user, @Param("date") LocalDateTime date);

    // البحث عن الإشعارات بواسطة النوع
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.type = :type")
    List<Notification> findByUserAndType(@Param("user") User user, @Param("type") String type);

    // عد الإشعارات غير المقروءة للمستخدم
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    Long countUnreadByUser(@Param("user") User user);

    // تحديث جميع إشعارات المستخدم إلى مقروءة
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);

    // تحديث إشعار محدد إلى مقروءة
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.user = :user")
    void markAsRead(@Param("id") Long id, @Param("user") User user);

    // حذف جميع إشعارات المستخدم
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user = :user")
    void deleteByUser(@Param("user") User user);

    // البحث عن الإشعارات المنتهية الصلاحية (أقدم من تاريخ معين)
    @Query("SELECT n FROM Notification n WHERE n.createdAt < :date")
    List<Notification> findExpiredNotifications(@Param("date") LocalDateTime date);
}