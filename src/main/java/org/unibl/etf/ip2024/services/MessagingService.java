package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.ConversationDTO;
import org.unibl.etf.ip2024.models.dto.MessageDTO;

import java.security.Principal;
import java.util.List;

@Service
public interface MessagingService {
    MessageDTO createMessage(Principal principal, MessageDTO messageDTO);
    List<ConversationDTO> getConversations(Principal principal);
    List<MessageDTO> getMessagesForConversation(Principal principal, Integer conversationUserId);
}
