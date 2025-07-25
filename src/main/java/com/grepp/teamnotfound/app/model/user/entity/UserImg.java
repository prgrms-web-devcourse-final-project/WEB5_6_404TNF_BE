package com.grepp.teamnotfound.app.model.user.entity;

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
@Table(name = "UserImgs")
@Getter
@Setter
public class UserImg extends BaseEntity implements ImageFile {

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
    private Long userImgId;

    @Column(length = 200)
    private String savePath;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ImgType type;

    @Column
    private String originName;

    @Column
    private String renamedName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public OffsetDateTime getDeletedAt() {
        return super.getDeletedAt();
    }

    @Override
    public FileDto toFileDto() {
        return new FileDto(
            this.originName,
            this.renamedName,
            "user",
            this.savePath
        );
    }
}

