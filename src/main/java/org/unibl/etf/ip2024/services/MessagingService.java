package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.MessageDTO;

@Service
public interface MessagingService {
    MessageDTO createMessage(MessageDTO messageDTO);
}
