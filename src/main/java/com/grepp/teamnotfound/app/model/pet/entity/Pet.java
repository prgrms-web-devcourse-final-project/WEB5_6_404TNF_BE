package com.grepp.teamnotfound.app.model.pet.entity;


import com.grepp.teamnotfound.app.model.pet.code.PetSize;
import com.grepp.teamnotfound.app.model.pet.code.PetType;
import com.grepp.teamnotfound.app.model.user.entity.User;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Pets")
@Getter
@Setter
public class Pet extends BaseEntity {

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
    private Long petId;

    @Column(nullable = true, length = 50)
    private String registNumber;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false)
    private LocalDate metday;

    @Column(length = 10)
    private String name;

    @Column(nullable = true)
    private Integer age;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PetType breed;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PetSize size;

    @Column
    private Double weight;

    @Column(nullable = false)
    private Boolean sex;

    @Column(nullable = false)
    private Boolean isNeutered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "pet", fetch = FetchType.LAZY)
    private PetImg petImg;
}
