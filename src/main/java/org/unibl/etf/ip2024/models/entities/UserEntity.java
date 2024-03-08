package org.unibl.etf.ip2024.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "username", nullable = false)
    private String username;
    @Basic
    @Column(name = "password", nullable = false)
    private String password;
    @Basic
    @Column(name = "email", nullable = false)
    private String email;
    @Basic
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Basic
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Basic
    @Column(name = "city", nullable = false)
    private String city;
    @Basic
    @Column(name = "role", nullable = false)
    private Object role;
    @Basic
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Basic
    @Column(name = "biography", length = -1)
    private String biography;
    @OneToMany(mappedBy = "users")
    private List<ActivityEntity> activities;
    @OneToMany(mappedBy = "user")
    private List<CommentEntity> comments;
    @OneToMany(mappedBy = "user")
    private List<FitnessProgramEntity> fitnessPrograms;
    @OneToMany(mappedBy = "userBySenderId")
    private List<MessageEntity> messagesBySenderId;
    @OneToMany(mappedBy = "userByRecipientId")
    private List<MessageEntity> messagesByRecipientId;
    @OneToMany(mappedBy = "user")
    private List<SubscriptionEntity> subscriptions;
    @OneToMany(mappedBy = "user")
    private List<UserProgramEntity> userPrograms;

}
