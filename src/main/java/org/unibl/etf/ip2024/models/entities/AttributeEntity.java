package org.unibl.etf.ip2024.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "attribute")
public class AttributeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "description", length = -1)
    private String user;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private CategoryEntity category;
    @JsonIgnore
    @OneToMany(mappedBy = "attribute")
    private List<AttributeValueEntity> attributeValues;

}
