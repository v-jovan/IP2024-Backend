package org.unibl.etf.ip2024.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Roles role;
    @Basic
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Basic
    @Column(name = "biography", length = -1)
    private String biography;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ActivityEntity> activities;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<CommentEntity> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<FitnessProgramEntity> fitnessPrograms;
    @JsonIgnore
    @OneToMany(mappedBy = "userBySenderId")
    private List<MessageEntity> messagesBySenderId;
    @JsonIgnore
    @OneToMany(mappedBy = "userByRecipientId")
    private List<MessageEntity> messagesByRecipientId;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<SubscriptionEntity> subscriptions;
    @JsonIgnore
    @OneToMany(mappedBy = "userByUserId")
    private List<UserProgramEntity> userPrograms;

}

enum Roles {
    USER,
    ADMIN,
    INSTRUCTOR
}
