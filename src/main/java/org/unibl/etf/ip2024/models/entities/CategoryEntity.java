package org.unibl.etf.ip2024.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "category")
public class CategoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "description", length = -1)
    private String description;
    @OneToMany(mappedBy = "categories")
    private List<AttributeEntity> attributes;
    @OneToMany(mappedBy = "category")
    private List<FitnessProgramEntity> fitnessPrograms;
    @OneToMany(mappedBy = "category")
    private List<SubscriptionEntity> subscriptions;

}
