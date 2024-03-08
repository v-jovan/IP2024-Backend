package org.unibl.etf.ip2024.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "fitness_program")
public class FitnessProgramEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    @Basic
    @Column(name = "instructor_id", nullable = false)
    private Integer instructorId;
    @Basic
    @Column(name = "location_id", nullable = false)
    private Integer locationId;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "description", nullable = false, length = -1)
    private String description;
    @Basic
    @Column(name = "difficulty_level", nullable = false)
    private Object difficultyLevel;
    @Basic
    @Column(name = "duration", nullable = false)
    private Integer duration;
    @Basic
    @Column(name = "price", nullable = false, precision = 2)
    private BigDecimal price;
    @Basic
    @Column(name = "youtube_url")
    private String youtubeUrl;
    @OneToMany(mappedBy = "fitnessProgram")
    private List<CommentEntity> comments;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private CategoryEntity category;
    @ManyToOne
    @JoinColumn(name = "instructor_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private LocationEntity location;
    @OneToMany(mappedBy = "fitnessProgram")
    private List<ProgramAttributeEntity> programAttributes;
    @OneToMany(mappedBy = "fitnessProgram")
    private List<UserProgramEntity> userPrograms;

}
