package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;

@Service
public interface SubscriptionService {
    void sendDailySubscriptionEmails();
}
