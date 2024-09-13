package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unibl.etf.ip2024.exceptions.UserNotFoundException;
import org.unibl.etf.ip2024.models.dto.ConversationDTO;
import org.unibl.etf.ip2024.models.dto.MessageDTO;
import org.unibl.etf.ip2024.models.entities.MessageEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.models.enums.Roles;
import org.unibl.etf.ip2024.repositories.MessageEntityRepository;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.MessagingService;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

    private final MessageEntityRepository messageEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Override
    public MessageDTO createMessage(Principal principal, MessageDTO messageDTO) {
        if (messageDTO.getSenderId() == null) {
            UserEntity sender = userEntityRepository.findByUsername(principal.getName())
                    .orElseThrow(UserNotFoundException::new);
            messageDTO.setSenderId(sender.getId());
        }
        UserEntity sender = userEntityRepository.findById(messageDTO.getSenderId())
                .orElseThrow(UserNotFoundException::new);
        UserEntity recipient = userEntityRepository.findById(messageDTO.getRecipientId())
                .orElseThrow(UserNotFoundException::new);

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSubject(messageDTO.getSubject());
        messageEntity.setContent(messageDTO.getContent());
        messageEntity.setSentAt(Timestamp.from(Instant.now()));
        messageEntity.setSender(sender);
        messageEntity.setRecipient(recipient);

        MessageEntity savedMessage = messageEntityRepository.save(messageEntity);

        MessageDTO savedMessageDTO = new MessageDTO();
        savedMessageDTO.setId(savedMessage.getId());
        savedMessageDTO.setSenderId(savedMessage.getSender().getId());
        savedMessageDTO.setRecipientId(savedMessage.getRecipient().getId());
        savedMessageDTO.setSubject(savedMessage.getSubject());
        savedMessageDTO.setContent(savedMessage.getContent());
        savedMessageDTO.setSentAt(savedMessage.getSentAt());

        return savedMessageDTO;
    }

    @Override
    public List<ConversationDTO> getConversations(Principal principal) {
        UserEntity user = userEntityRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
        Integer userId = user.getId();

        List<MessageEntity> messages = messageEntityRepository.findAllBySenderIdOrRecipientId(userId, userId);

        Map<Integer, MessageEntity> lastMessages = new HashMap<>();
        Map<Integer, Boolean> unreadStatus = new HashMap<>();

        for (MessageEntity message : messages) {
            UserEntity otherUser = message.getSender().getId().equals(userId)
                    ? message.getRecipient()
                    : message.getSender();
            
            if (!otherUser.getRole().equals(Roles.INSTRUCTOR)) {
                Integer otherUserId = otherUser.getId();

                if (!lastMessages.containsKey(otherUserId) ||
                        lastMessages.get(otherUserId).getSentAt().before(message.getSentAt())) {
                    lastMessages.put(otherUserId, message);
                }

                if (message.getRecipient().getId().equals(userId) && message.getReadAt() == null) {
                    unreadStatus.put(otherUserId, true);
                }
            }
        }

        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        for (Map.Entry<Integer, MessageEntity> entry : lastMessages.entrySet()) {
            Integer otherUserId = entry.getKey();
            MessageEntity message = entry.getValue();
            ConversationDTO conversation = buildConversationDTO(message, userId);

            conversation.setUnread(unreadStatus.getOrDefault(otherUserId, false));

            conversationDTOs.add(conversation);
        }

        conversationDTOs.sort((c1, c2) -> c2.getLastMessageTime().compareTo(c1.getLastMessageTime()));

        return conversationDTOs;
    }

    @Override
    @Transactional
    public List<MessageDTO> getMessagesForConversation(Principal principal, Integer conversationUserId) {
        UserEntity currentUser = userEntityRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
        Integer currentUserId = currentUser.getId();

        List<MessageEntity> messages = messageEntityRepository.findMessagesBetweenUsers(currentUserId, conversationUserId);

        List<MessageEntity> unreadMessages = new ArrayList<>();
        for (MessageEntity message : messages) {
            if (message.getRecipient().getId().equals(currentUserId) && message.getReadAt() == null) {
                message.setReadAt(Timestamp.from(Instant.now()));
                unreadMessages.add(message);
            }
        }

        if (!unreadMessages.isEmpty()) {
            messageEntityRepository.saveAll(unreadMessages);
        }

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO convertToDTO(MessageEntity messageEntity) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(messageEntity.getId());
        messageDTO.setSenderId(messageEntity.getSender().getId());
        messageDTO.setRecipientId(messageEntity.getRecipient().getId());
        messageDTO.setContent(messageEntity.getContent());
        messageDTO.setSentAt(messageEntity.getSentAt());
        messageDTO.setReadAt(messageEntity.getReadAt());
        return messageDTO;
    }

    private ConversationDTO buildConversationDTO(MessageEntity message, Integer userId) {
        ConversationDTO conversation = new ConversationDTO();
        UserEntity otherUser = message.getSender().getId().equals(userId)
                ? message.getRecipient()
                : message.getSender();
        conversation.setUserId(otherUser.getId());
        conversation.setUsername(getDisplayName(otherUser));
        conversation.setLastMessage(message.getContent());
        conversation.setLastMessageTime(message.getSentAt());
        conversation.setAvatarUrl(otherUser.getAvatarUrl());
        return conversation;
    }

    private static String getDisplayName(UserEntity user) {
        if (user == null) {
            return "";
        }
        String displayName;
        if (user.getFirstName() != null && user.getLastName() != null) {
            displayName = user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null) {
            displayName = user.getFirstName();
        } else if (user.getLastName() != null) {
            displayName = user.getLastName();
        } else {
            displayName = user.getUsername();
        }
        return displayName;
    }


}
