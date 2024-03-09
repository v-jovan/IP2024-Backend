package org.unibl.etf.ip2024.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "message")
public class MessageEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;
    @Basic
    @Column(name = "sent_at", nullable = false)
    private Timestamp sentAt;
    @Basic
    @Column(name = "read_at")
    private Timestamp readAt;
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private UserEntity userBySenderId;
    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id", nullable = false)
    private UserEntity userByRecipientId;

}
