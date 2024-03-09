package org.unibl.etf.ip2024.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Data
@Entity
@Table(name = "user_program")
public class UserProgramEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @Basic
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    @Basic
    @Column(name = "end_date", nullable = false)
    private Date endDate;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity userByUserId;
    @ManyToOne
    @JoinColumn(name = "program_id", referencedColumnName = "id", nullable = false)
    private FitnessProgramEntity fitnessProgramByProgramId;

}

enum Status {
    ACTIVE, INACTIVE
}
