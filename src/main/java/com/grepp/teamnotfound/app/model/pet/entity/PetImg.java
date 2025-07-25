package com.grepp.teamnotfound.app.model.pet.entity;

import com.grepp.teamnotfound.infra.util.file.ImageFile;
import com.grepp.teamnotfound.infra.code.ImgType;
import com.grepp.teamnotfound.infra.entity.BaseEntity;
import com.grepp.teamnotfound.infra.util.file.FileDto;
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
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "PetImgs")
@Getter
@Setter
public class PetImg extends BaseEntity implements ImageFile {

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
    @Enumerated(EnumType.STRING) // 이 부분 추가
    private ImgType type;

    @Column(nullable = false)
    private String originName;

    @Column(nullable = false)
    private String renamedName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Override
    public OffsetDateTime getDeletedAt() {
        return super.getDeletedAt();
    }

    @Override
    public FileDto toFileDto() {
        return new FileDto(
            this.originName,
            this.renamedName,
            "pet",
            this.savePath
        );
    }
}

