package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.UserNotFoundException;
import org.unibl.etf.ip2024.models.dto.MessageDTO;
import org.unibl.etf.ip2024.models.entities.MessageEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.repositories.MessageEntityRepository;
import org.unibl.etf.ip2024.repositories.UserEntityRepository;
import org.unibl.etf.ip2024.services.MessagingService;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

    private final MessageEntityRepository messageEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        UserEntity sender = userEntityRepository.findById(messageDTO.getSenderId())
                .orElseThrow(UserNotFoundException::new);
        UserEntity recipient = userEntityRepository.findById(messageDTO.getRecipientId())
                .orElseThrow(UserNotFoundException::new);

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSubject(messageDTO.getSubject());
        messageEntity.setContent(messageDTO.getContent());
        messageEntity.setSentAt(Timestamp.from(Instant.now()));
        messageEntity.setUserBySenderId(sender);
        messageEntity.setUserByRecipientId(recipient);

        MessageEntity savedMessage = messageEntityRepository.save(messageEntity);

        MessageDTO savedMessageDTO = new MessageDTO();
        savedMessageDTO.setId(savedMessage.getId());
        savedMessageDTO.setSenderId(savedMessage.getUserBySenderId().getId());
        savedMessageDTO.setRecipientId(savedMessage.getUserByRecipientId().getId());
        savedMessageDTO.setSubject(savedMessage.getSubject());
        savedMessageDTO.setContent(savedMessage.getContent());
        savedMessageDTO.setSentAt(savedMessage.getSentAt());

        return savedMessageDTO;
    }
}
