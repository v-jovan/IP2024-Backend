package org.unibl.etf.ip2024.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "program_attribute")
public class ProgramAttributeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "fitness_program_id", referencedColumnName = "id", nullable = false)
    private FitnessProgramEntity fitnessProgram;
    @ManyToOne
    @JoinColumn(name = "attribute_value_id", referencedColumnName = "id", nullable = false)
    private AttributeValueEntity attributeValue;

}
