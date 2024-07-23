package org.unibl.etf.ip2024.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.unibl.etf.ip2024.models.enums.DifficultyLevel;

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
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "description", nullable = false, length = -1)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;
    @Basic
    @Column(name = "duration", nullable = false)
    private Integer duration;
    @Basic
    @Column(name = "price", nullable = false, precision = 2)
    private BigDecimal price;
    @Basic
    @Column(name = "youtube_url")
    private String youtubeUrl;
    @JsonIgnore
    @OneToMany(mappedBy = "fitnessProgram")
    private List<CommentEntity> comments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private CategoryEntity category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private LocationEntity location;
    @JsonIgnore
    @OneToMany(mappedBy = "fitnessProgram")
    private List<ProgramAttributeEntity> programAttributes;
    @JsonIgnore
    @OneToMany(mappedBy = "fitnessProgramByProgramId")
    private List<UserProgramEntity> userPrograms;

}

