package org.unibl.etf.ip2024.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.unibl.etf.ip2024.models.enums.Roles;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class UserEntity implements UserDetails {
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
    @Column(name = "first_name")
    private String firstName;
    @Basic
    @Column(name = "last_name")
    private String lastName;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private CityEntity city;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}