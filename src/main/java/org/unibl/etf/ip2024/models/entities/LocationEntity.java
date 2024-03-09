package org.unibl.etf.ip2024.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "location")
public class LocationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @JsonIgnore
    @OneToMany(mappedBy = "location")
    private List<FitnessProgramEntity> fitnessPrograms;

}
