package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendActivationEmail(String to, String activationLink);
}
