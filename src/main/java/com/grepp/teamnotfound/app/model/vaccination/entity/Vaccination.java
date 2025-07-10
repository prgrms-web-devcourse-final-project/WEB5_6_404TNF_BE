package com.grepp.teamnotfound.app.model.vaccination.entity;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.vaccination.code.VaccineType;
import com.grepp.teamnotfound.infra.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Vaccinations")
@Getter
@Setter
public class Vaccination extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
        name = "primary_sequence",
        sequenceName = "primary_sequence",
        allocationSize = 1,
        initialValue = 10000
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "primary_sequence"
    )
    private Long vaccinationId;

    @Column(nullable = false)
    private LocalDate vaccineAt;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VaccineType vaccineType;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Boolean isVaccine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

}
