package com.gabriel.twoforms.repository;

import com.gabriel.twoforms.entity.NotificationData;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NotificationDataRepository extends CrudRepository<NotificationData, String> {
    List<NotificationData> findByRecipientId(String recipientId);
    long countByRecipientIdAndRead(String recipientId, boolean read);
}
