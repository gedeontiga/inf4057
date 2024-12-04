package com.m1fonda.service_notif.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_notif.entities.Notification;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByNotificationId(String notificationId);

    List<Notification> findByAgencyNum(String agencyNum);

    List<Notification> findByUserEmail(String receiverEmail);

    void deleteByNotificationId(String notificationId);

    void deleteByUserEmail(String receiverEmail);

    void deleteByAgencyNum(String agencyNum);
}
