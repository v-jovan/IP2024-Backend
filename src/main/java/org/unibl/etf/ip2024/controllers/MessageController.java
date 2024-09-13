package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.ip2024.models.dto.ConversationDTO;
import org.unibl.etf.ip2024.models.dto.MessageDTO;
import org.unibl.etf.ip2024.services.MessagingService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessagingService messagingService;

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(Principal principal, @RequestBody MessageDTO messageDTO) {
        MessageDTO savedMessage = messagingService.createMessage(principal, messageDTO);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getConversations(Principal principal) {
        List<ConversationDTO> conversations = messagingService.getConversations(principal);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversation/{conversationUserId}")
    public ResponseEntity<List<MessageDTO>> getMessagesForConversation(
            Principal principal,
            @PathVariable Integer conversationUserId) {
        List<MessageDTO> messages = messagingService.getMessagesForConversation(principal, conversationUserId);
        return ResponseEntity.ok(messages);
    }

}
