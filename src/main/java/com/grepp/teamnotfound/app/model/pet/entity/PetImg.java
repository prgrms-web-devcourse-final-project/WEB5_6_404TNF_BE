package com.grepp.teamnotfound.app.model.pet.entity;

import com.grepp.teamnotfound.infra.code.ImgType;
import com.grepp.teamnotfound.infra.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "PetImgs")
@Getter
@Setter
public class PetImg extends BaseEntity {

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
    private Long petImgId;

    @Column(nullable = false)
    private String savePath;

    @Column(nullable = false, length = 20)
    private ImgType type;

    @Column(nullable = false)
    private String originName;

    @Column(nullable = false)
    private String renamedName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, unique = true)
    private Pet pet;

}

